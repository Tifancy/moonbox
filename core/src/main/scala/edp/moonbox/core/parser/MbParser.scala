package edp.moonbox.core.parser

import edp.moonbox.common.EdpLogging
import edp.moonbox.core.MbCommand
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream, IntStream}

/**
  * Created by edp on 9/6/17.
  */
class MbParser extends EdpLogging {

	val cmdBuilder = new MbCmdBuilder

	def parse(sql: String): MbCommand = {
		val mbSqlLexer = new CMDLexer(new ANTLRNoCaseStringStream(sql))
		val tokenStream = new CommonTokenStream(mbSqlLexer)
		val mbSqlParser = new CMDParser(tokenStream)
		val cmd = cmdBuilder.visitSingleCmd(mbSqlParser.singleCmd())
		logInfo(s"parse $sql to cmd $cmd")
		cmd
	}

	class ANTLRNoCaseStringStream(input: String) extends ANTLRInputStream(input) {
		override def LA(i: Int): Int = {
			val la = super.LA(i)
			if (la == 0 || la == IntStream.EOF) la
			else Character.toUpperCase(la)
		}
	}
}
