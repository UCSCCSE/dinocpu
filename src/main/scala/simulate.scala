// Main entry point for simulation
package CODCPU

import firrtl.{ExecutionOptionsManager, HasFirrtlOptions}
import treadle.{HasTreadleOptions, TreadleOptionsManager, TreadleTester}
import java.io.{File, PrintWriter, RandomAccessFile}

import chisel3.{ChiselExecutionFailure, ChiselExecutionSuccess, HasChiselExecutionOptions}
import net.fornwall.jelf.ElfFile

import scala.collection.SortedMap

/**
 * Simple object with only a main function to run the treadle simulation.
 * When run, this will begin execution and continue until something happens...
 *
 * {{{
 *  sbt> runMain CODCPU.simulate <riscv binary> <CPU type> [max cycles]
 * }}}
 */
object simulate {
  var helptext = "usage: simulate <riscv binary> <CPU type> [max cycles]"

    def elfToHex(filename: String, outfile: String) = {
        val elf = ElfFile.fromFile(new java.io.File(filename))
        val sections = Seq(".text", ".data") // These are the sections we want to pull out
        // address to put the data -> offset into the binary, size of section
        var info : SortedMap[Long, (Long, Long)] = SortedMap()
        // Search for these section names
        for (i <- 1 to elf.num_sh - 1) {
            val section =  elf.getSection(i)
            if (sections.contains(section.getName)) {
                //println("Found "+section.address + " " + section.section_offset + " " + section.size)
                info += section.address -> (section.section_offset, section.size)
            }
        }
        require(info.size == sections.length, "Couldn't find all of the sections in the binary!")

        // Now, we want to create a new file to load into our memory
        val output = new PrintWriter(new File(outfile))
        val f = new RandomAccessFile(filename, "r")
        // println("Length: "+ f.length)
        var location = 0
        for ((address, (offset, size)) <- info) {
            //println(s"Skipping until $address")
            while (location < address) {
                require(location + 3 < address, "Assuming addresses aligned to 4 bytes")
                output.write("00000000\n")
                location += 4
            }
            //println(s"Writing $size bytes")
            val data = new Array[Byte](size.toInt)
            f.seek(offset)
            f.read(data)
            var s = List[String]()
            for (byte <- data) {
                s = s :+ ("%02X" format byte)
                location += 1
                if (location % 4 == 0) {
                    // Once we've read 4 bytes, swap endianness
                    output.write(s(3)+s(2)+s(1)+s(0)+"\n")
                    s = List[String]()
                }
            }
            //println(s"Wrote until $location")
        }
        output.close

        // Return the final PC value we're looking for
        elf.getELFSymbol("_last").value
    }

  def main(args: Array[String]): Unit = {
    require(args.length >= 2, "Error: Expected at least two argument\n" + helptext)

    val optionsManager = new SimulatorOptionsManager

    if (optionsManager.parser.parse(args)) {
        optionsManager.setTargetDirName("simulator_run_dir")
    } else {
        None
    }

    val rawName = optionsManager.targetDirName + "/executable.hex"

    val endPC = elfToHex(args(0), rawName)

    val conf = new CPUConfig()
    conf.cpuType = args(1)
    // It would be nice to put this in the "simulator_run_dir", but fighting these options isn't worth my time, right now.
    conf.memFile = rawName

    optionsManager.firrtlOptions = optionsManager.firrtlOptions.copy(compilerName = "low")
    val annos = firrtl.Driver.getAnnotations(optionsManager)
    optionsManager.firrtlOptions = optionsManager.firrtlOptions.copy(annotations = annos.toList)

    val simulator = chisel3.Driver.execute(optionsManager, () => new Top(conf)) match {
      case ChiselExecutionSuccess(Some(circuit), _, Some(firrtlExecutionResult)) =>
        firrtlExecutionResult match {
          case firrtl.FirrtlExecutionSuccess(_, compiledFirrtl) =>
            new TreadleTester(compiledFirrtl, optionsManager)
          case firrtl.FirrtlExecutionFailure(message) =>
            throw new Exception(s"FirrtlBackend: Compile failed. Message: $message")
        }
      case _ =>
        throw new Exception("Problem with compilation")
    }

    simulator.reset(5)
    var cycles = 0
    val maxCycles = if (optionsManager.simulatorOptions.maxCycles > 0) optionsManager.simulatorOptions.maxCycles else 100000
    while (simulator.peek("cpu.pc") != endPC && cycles < maxCycles) {
        simulator.step(1)
        cycles += 1
    }
  }
}

case class SimulatorOptions(
                            maxCycles           : Int              = 0
    )
    extends firrtl.ComposableOptions {
}

trait HasSimulatorOptions {
    self: ExecutionOptionsManager =>

    var simulatorOptions = SimulatorOptions()

    parser.note("simulator-options")

    parser.opt[Int]("max-cycles")
        .abbr("mx")
        .valueName("<long-value>")
        .foreach {x =>
            simulatorOptions.copy(maxCycles = x)
        }
        .text("Max number of cycles to simulate. Default is 0, to continue simulating")
}

class SimulatorOptionsManager extends TreadleOptionsManager with HasSimulatorSuite

trait HasSimulatorSuite extends TreadleOptionsManager with HasChiselExecutionOptions with HasFirrtlOptions with HasTreadleOptions with HasSimulatorOptions {
    self : ExecutionOptionsManager =>
}
