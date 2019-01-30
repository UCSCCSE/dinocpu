// Tests for Lab 3. Feel free to modify and add more tests here.
// If you name your test class something that ends with "TesterLab3" it will
// automatically be run when you use `Lab3 / test` at the sbt prompt.


package dinocpu

import chisel3._

import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedRTypeTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedRTypeTesterLab3'
  * }}}
  */
class PipelinedRTypeTesterLab3 extends CPUFlatSpec {
  behavior of "Pipelined"
  for (test <- InstTests.rtype) {
    it should s"run R-type instruction ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "pipelined") should be(true)
    }
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedITypeTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedITypeTesterLab3'
  * }}}
  *
*/
class PipelinedITypeTesterLab3 extends CPUFlatSpec {

  val maxInt = BigInt("FFFFFFFF", 16)

  def twoscomp(v: BigInt) : BigInt = {
    if (v < 0) {
      return maxInt + v + 1
    } else {
      return v
    }
  }

  val tests = List[CPUTestCase](
		CPUTestCase("addi1",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(),
								Map(0 -> 0, 10 -> 17),
								Map(), Map()),
		CPUTestCase("addi2",
                Map("single-cycle" -> 2, "five-cycle" -> 12, "pipelined" -> 6),
                Map(),
								Map(0 -> 0, 10 -> 17, 11 -> 93),
								Map(), Map()),
		CPUTestCase("slli",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> 1),
								Map(0 -> 0, 5 -> 1, 6 -> 128),
								Map(), Map()),
		CPUTestCase("srai",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> 1024),
								Map(0 -> 0, 5 -> 1024, 6 -> 8),
								Map(), Map()),
		CPUTestCase("srai",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> twoscomp(-1024)),
								Map(0 -> 0, 5 -> twoscomp(-1024), 6 -> twoscomp(-8)),
								Map(), Map(), "-negative"),
		CPUTestCase("srli",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> 128),
								Map(0 -> 0, 5 -> 128, 6 -> 1),
								Map(), Map()),
		CPUTestCase("andi",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> 456),
								Map(0 -> 0, 5 -> 456, 6 -> 200),
								Map(), Map()),
		CPUTestCase("ori",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> 456),
								Map(0 -> 0, 5 -> 456, 6 -> 511),
								Map(), Map()),
		CPUTestCase("xori",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> 456),
								Map(0 -> 0, 5 -> 456, 6 -> 311),
								Map(), Map()),
		CPUTestCase("slti",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> twoscomp(-1)),
								Map(0 -> 0, 5 -> twoscomp(-1),6->1),
								Map(), Map()),
		CPUTestCase("sltiu",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(5 -> twoscomp(-1)),
								Map(0 -> 0, 5 -> twoscomp(-1), 6 -> 0),
								Map(), Map())
 )
  for (test <- tests) {
    "Pipelined" should s"run I-Type instruction ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "pipelined") should be(true)
    }
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedLoadTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedLoadTesterLab3'
  * }}}
  *
*/
class PipelinedLoadTesterLab3 extends CPUFlatSpec {

  val tests = List[CPUTestCase](
		CPUTestCase("lw1",
                Map("single-cycle" -> 1, "five-cycle" -> 8, "pipelined" -> 5),
                Map(),
								Map(5 -> BigInt("ffffffff", 16)),
								Map(), Map()),
		CPUTestCase("lwfwd",
                Map("single-cycle" -> 2, "five-cycle" -> 20, "pipelined" -> 12),
                Map(5 -> BigInt("ffffffff", 16), 10 -> 5),
								Map(5 -> 1, 10 -> 6),
								Map(), Map())
 )
  for (test <- tests) {
    "Pipelined" should s"run load instruction test ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "pipelined") should be(true)
    }
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedUTypeTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedUTypeTesterLab3'
  * }}}
  *
*/
class PipelinedUTypeTesterLab3 extends CPUFlatSpec {

  val tests = List[CPUTestCase](
		CPUTestCase("auipc0",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(10 -> 1234),
								Map(10 -> 0),
								Map(), Map()),
		CPUTestCase("auipc1",
                Map("single-cycle" -> 2, "five-cycle" -> 6, "pipelined" -> 10),
                Map(10 -> 1234),
								Map(10 -> 4),
								Map(), Map()),
		CPUTestCase("auipc2",
                Map("single-cycle" -> 2, "five-cycle" -> 6, "pipelined" -> 6),
                Map(10 -> 1234),
								Map(10 -> (17 << 12)),
								Map(), Map()),
		CPUTestCase("auipc3",
                Map("single-cycle" -> 2, "five-cycle" -> 6, "pipelined" -> 10),
                Map(10 -> 1234),
								Map(10 -> ((17 << 12) + 4)),
								Map(), Map()),
		CPUTestCase("lui0",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(10 -> 1234),
								Map(10 -> 0),
								Map(), Map()),
		CPUTestCase("lui1",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(10 -> 1234),
								Map(10 -> 4096),
								Map(), Map())
 )
  for (test <- tests) {
  "Pipelined" should s"run auipc/lui instruction test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "pipelined") should be(true)
	}
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedStoreTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedStoreTesterLab3'
  * }}}
  *
*/
class PipelinedStoreTesterLab3 extends CPUFlatSpec {

  val tests = List[CPUTestCase](
		CPUTestCase("sw",
                Map("single-cycle" -> 6, "five-cycle" -> 10, "pipelined" -> 10),
                Map(5 -> 1234),
								Map(6 -> 1234),
								Map(), Map(0x100 -> 1234))
 )
  for (test <- tests) {
  "Pipelined" should s"run add Store instruction test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "pipelined") should be(true)
	}
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedLoadStoreTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedLoadStoreTesterLab3'
  * }}}
  *
*/
class PipelinedLoadStoreTesterLab3 extends CPUFlatSpec {

  val tests = List[CPUTestCase](
		CPUTestCase("lb",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(),
								Map(5 -> BigInt("04", 16)),
								Map(), Map()),
		CPUTestCase("lh",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(),
								Map(5 -> BigInt("0304", 16)),
								Map(), Map()),
		CPUTestCase("lbu",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(),
								Map(5 -> BigInt("f4", 16)),
								Map(), Map()),
		CPUTestCase("lhu",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(),
								Map(5 -> BigInt("f3f4", 16)),
								Map(), Map()),
		CPUTestCase("lb1",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(),
								Map(5 -> BigInt("fffffff4", 16)),
								Map(), Map()),
		CPUTestCase("lh1",
                Map("single-cycle" -> 1, "five-cycle" -> 5, "pipelined" -> 5),
                Map(),
								Map(5 -> BigInt("fffff3f4", 16)),
								Map(), Map()),
		CPUTestCase("sb",
                Map("single-cycle" -> 6, "five-cycle" -> 10, "pipelined" -> 10),
                Map(5 -> 1),
								Map(6 -> 1),
								Map(), Map(0x100 -> BigInt("ffffff01", 16))),
		CPUTestCase("sh",
                Map("single-cycle" -> 6, "five-cycle" -> 10, "pipelined" -> 10),
                Map(5 -> 1),
								Map(6 -> 1),
								Map(), Map(0x100 -> BigInt("ffff0001", 16)))
 )
  for (test <- tests) {
  "Pipelined" should s"run load/store insturction test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "pipelined") should be(true)
	}
  }
}



/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedBranchTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedBranchTesterLab3'
  * }}}
  *
*/
class PipelinedBranchTesterLab3 extends CPUFlatSpec {
  behavior of "Pipelined"
  for (test <- InstTests.branch) {
    it should s"run branch instruction test ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "pipelined") should be(true)
    }
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedJALTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedJALTesterLab3'
  * }}}
  *
*/
class PipelinedJALTesterLab3 extends CPUFlatSpec {

  val tests = List[CPUTestCase](
    CPUTestCase("jal",
                Map("single-cycle" -> 2, "five-cycle" -> 20, "pipelined" -> 20),
                Map(5 -> 1234),
								Map(0 -> 0, 5 -> 1234, 6 -> 1234, 1 -> 4),
								Map(), Map())
 )
  for (test <- tests) {
  "Pipelined" should s"run JAL instruction test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "pipelined") should be(true)
	}
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedJALRTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedJALRTesterLab3'
  * }}}
  *
*/
class PipelinedJALRTesterLab3 extends CPUFlatSpec {

  val tests = List[CPUTestCase](
    CPUTestCase("jalr0",
                Map("single-cycle" -> 2, "five-cycle" -> 10, "pipelined" -> 10),
                Map(5 -> 1234, 10 -> 28),
								Map(0 -> 0, 5 -> 1234, 6 -> 1234, 1 -> 4),
								Map(), Map()),
    CPUTestCase("jalr1",
                Map("single-cycle" -> 2, "five-cycle" -> 10, "pipelined" -> 10),
                Map(5 -> 1234, 10 -> 20),
								Map(0 -> 0, 5 -> 1234, 6 -> 1234, 1 -> 4),
								Map(), Map())
 )
  for (test <- tests) {
  "Pipelined" should s"run JALR instruction test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "pipelined") should be(true)
	}
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.PipelinedApplicationsTesterLab3
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.PipelinedApplicationsTesterLab3'
  * }}}
  *
*/
class PipelinedApplicationsTesterLab3 extends CPUFlatSpec {

  val tests = List[CPUTestCase](
    CPUTestCase("fibonacci",
              	Map("single-cycle" -> 300, "five-cycle" -> 6, "pipelined" -> 400),
              	Map(6->11),
								Map(6->11,5->89),
								Map(), Map()),
    CPUTestCase("naturalsum",
               	Map("single-cycle" -> 200, "five-cycle" -> 6, "pipelined" -> 300),
                Map(),
								Map(5->55),
								Map(), Map()),
    CPUTestCase("multiplier",
          	Map("single-cycle" -> 1000, "five-cycle" -> 6, "pipelined" -> 1000),
        	Map(5->23,6->20),
								Map(5->23*20),
								Map(), Map()),
    CPUTestCase("divider",
                Map("single-cycle" -> 1000, "five-cycle" -> 6, "pipelined" -> 1500),
                Map(5->1260,6->30),
								Map(7->42),
								Map(), Map()),
    CPUTestCase("test",
                Map("single-cycle" -> 1000, "five-cycle" -> 6, "pipelined" -> 20),
                Map(5->1234),
								Map(5->2468,6->2468,7->2468,28->2468,29->2468,30->2468,31->2468),
								Map(), Map())

 )
  for (test <- tests) {
  "Pipelined" should s"run application test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "pipelined") should be(true)
	}
  }
}





