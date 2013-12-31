package uk.co.cwspencer.gdb.messages;

import org.junit.Assert;
import org.junit.Test;
import uk.co.cwspencer.gdb.gdbmi.GdbMiParser;
import uk.co.cwspencer.gdb.gdbmi.GdbMiRecord;
import uk.co.cwspencer.gdb.gdbmi.GdbMiResultRecord;
import uk.co.cwspencer.gdb.messages.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Tests for GdbMiMessageConverter.
 */
public class TestGdbMiMessageConverter
{
	/**
	 * Verifies the correct conversion of a 'connected' message.
	 */
	@Test
	public void testConnectedEvent() throws UnsupportedEncodingException
	{
		// Parse the message
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"^connected,addr=\"0xfe00a300\",func=\"??\",args=[]\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));
		List<GdbMiRecord> records = parser.getRecords();

		// Convert the message
		GdbMiResultRecord record = (GdbMiResultRecord) records.get(0);
		Object object = GdbMiMessageConverter.processRecord(record);
		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof GdbConnectedEvent);

		GdbConnectedEvent connectedEvent = (GdbConnectedEvent) object;
		Assert.assertEquals(new Long(0xfe00a300l), connectedEvent.address);
		Assert.assertNull(connectedEvent.function);
		Assert.assertTrue(connectedEvent.arguments.isEmpty());
	}

	/**
	 * Verifies the correct conversion of an 'error' message.
	 */
	@Test
	public void testErrorEvent() throws UnsupportedEncodingException
	{
		// Parse the message
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"^error,msg=\"mi_cmd_exec_interrupt: Inferior not executing.\"\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));
		List<GdbMiRecord> records = parser.getRecords();

		// Convert the message
		GdbMiResultRecord record = (GdbMiResultRecord) records.get(0);
		Object object = GdbMiMessageConverter.processRecord(record);
		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof GdbErrorEvent);

		GdbErrorEvent errorEvent = (GdbErrorEvent) object;
		Assert.assertEquals("mi_cmd_exec_interrupt: Inferior not executing.", errorEvent.message);
	}

	/**
	 * Verifies the correct conversion of an 'exit' message.
	 */
	@Test
	public void testExitEvent() throws UnsupportedEncodingException
	{
		// Parse the message
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"^exit\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));
		List<GdbMiRecord> records = parser.getRecords();

		// Convert the message
		GdbMiResultRecord record = (GdbMiResultRecord) records.get(0);
		Object object = GdbMiMessageConverter.processRecord(record);
		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof GdbExitEvent);
	}

	/**
	 * Verifies the correct conversion of a 'stopped' message.
	 */
	@Test
	public void testStoppedEvent() throws UnsupportedEncodingException
	{
		// Parse the message
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"*stopped," +
			"reason=\"breakpoint-hit\"," +
			"disp=\"keep\"," +
			"bkptno=\"1\"," +
			"thread-id=\"0\"," +
			"stopped-threads=\"all\"," +
			"frame={" +
				"addr=\"0x08048564\"," +
				"func=\"main\"," +
				"args=[{" +
					"name=\"argc\"," +
					"value=\"1\"}," +
					"{name=\"argv\"," +
					"value=\"0xbfc4d4d4\"}]," +
				"file=\"myprog.c\"," +
				"fullname=\"/home/nickrob/myprog.c\"," +
				"line=\"68\"}\r\n" +
			"*stopped,stopped-threads=[\"1\",\"2\"]\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));
		List<GdbMiRecord> records = parser.getRecords();

		// Convert the message
		{
			GdbMiResultRecord record = (GdbMiResultRecord) records.get(0);
			Object object = GdbMiMessageConverter.processRecord(record);
			Assert.assertNotNull(object);
			Assert.assertTrue(object instanceof GdbStoppedEvent);

			GdbStoppedEvent stoppedEvent = (GdbStoppedEvent) object;
			Assert.assertEquals(GdbStoppedEvent.Reason.BreakpointHit, stoppedEvent.reason);
			Assert.assertEquals(GdbBreakpoint.BreakpointDisposition.Keep,
				stoppedEvent.breakpointDisposition);
			Assert.assertEquals(new Integer(1), stoppedEvent.breakpointNumber);
			Assert.assertEquals(new Integer(0), stoppedEvent.threadId);
			Assert.assertEquals(true, stoppedEvent.allStopped);
			Assert.assertNull(stoppedEvent.stoppedThreads);

			Assert.assertEquals(new Long(0x08048564), stoppedEvent.frame.address);
			Assert.assertEquals("main", stoppedEvent.frame.function);
			Assert.assertEquals("myprog.c", stoppedEvent.frame.fileRelative);
			Assert.assertEquals("/home/nickrob/myprog.c", stoppedEvent.frame.fileAbsolute);
			Assert.assertEquals(new Integer(68), stoppedEvent.frame.line);

			Assert.assertEquals(2, stoppedEvent.frame.arguments.size());
			Assert.assertEquals("1", stoppedEvent.frame.arguments.get("argc"));
			Assert.assertEquals("0xbfc4d4d4", stoppedEvent.frame.arguments.get("argv"));
		}

		{
			GdbMiResultRecord record = (GdbMiResultRecord) records.get(1);
			Object object = GdbMiMessageConverter.processRecord(record);
			Assert.assertNotNull(object);
			Assert.assertTrue(object instanceof GdbStoppedEvent);

			GdbStoppedEvent stoppedEvent = (GdbStoppedEvent) object;
			Assert.assertEquals(false, stoppedEvent.allStopped);
			Assert.assertEquals(2, stoppedEvent.stoppedThreads.size());
			Assert.assertEquals(new Integer(1), stoppedEvent.stoppedThreads.get(0));
			Assert.assertEquals(new Integer(2), stoppedEvent.stoppedThreads.get(1));
		}
	}

	/**
	 * Verifies the correct conversion of a 'running' message.
	 */
	@Test
	public void testRunningEvent() throws UnsupportedEncodingException
	{
		// Parse the message
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"*running,thread-id=\"2\"\r\n" +
			"*running,thread-id=\"all\"\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));
		List<GdbMiRecord> records = parser.getRecords();

		// Convert the message
		{
			GdbMiResultRecord record = (GdbMiResultRecord) records.get(0);
			Object object = GdbMiMessageConverter.processRecord(record);
			Assert.assertNotNull(object);
			Assert.assertTrue(object instanceof GdbRunningEvent);

			GdbRunningEvent runningEvent = (GdbRunningEvent) object;
			Assert.assertEquals(false, runningEvent.allThreads);
			Assert.assertEquals(new Integer(2), runningEvent.threadId);
		}

		{
			GdbMiResultRecord record = (GdbMiResultRecord) records.get(1);
			Object object = GdbMiMessageConverter.processRecord(record);
			Assert.assertNotNull(object);
			Assert.assertTrue(object instanceof GdbRunningEvent);

			GdbRunningEvent runningEvent = (GdbRunningEvent) object;
			Assert.assertEquals(true, runningEvent.allThreads);
			Assert.assertNull(runningEvent.threadId);
		}
	}

	/**
	 * Verifies the correct conversion of a stack trace message.
	 */
	@Test
	public void testStackTrace() throws UnsupportedEncodingException
	{
		// Parse the message
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"^done," +
			"stack=[" +
				"frame={" +
					"level=\"0\"," +
					"addr=\"0x00010734\"," +
					"func=\"callee4\"," +
					"file=\"../../../devo/gdb/testsuite/gdb.mi/basics.c\"," +
					"fullname=\"/home/foo/bar/devo/gdb/testsuite/gdb.mi/basics.c\"," +
					"line=\"8\"}," +
				"frame={" +
					"level=\"1\"," +
					"addr=\"0x0001076c\"," +
					"func=\"callee3\"," +
					"file=\"../../../devo/gdb/testsuite/gdb.mi/basics.c\"," +
					"fullname=\"/home/foo/bar/devo/gdb/testsuite/gdb.mi/basics.c\"," +
					"line=\"17\"}]\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));
		List<GdbMiRecord> records = parser.getRecords();

		// Convert the message
		GdbMiResultRecord record = (GdbMiResultRecord) records.get(0);
		Object object = GdbMiMessageConverter.processRecord(record, "-stack-list-frames");
		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof GdbStackTrace);

		GdbStackTrace stackTrace = (GdbStackTrace) object;
		Assert.assertEquals(2, stackTrace.stack.size());

		{
			GdbStackFrame frame = stackTrace.stack.get(0);
			Assert.assertEquals(new Integer(0), frame.level);
			Assert.assertEquals(new Long(0x00010734), frame.address);
			Assert.assertEquals("callee4", frame.function);
			Assert.assertEquals("../../../devo/gdb/testsuite/gdb.mi/basics.c", frame.fileRelative);
			Assert.assertEquals("/home/foo/bar/devo/gdb/testsuite/gdb.mi/basics.c",
				frame.fileAbsolute);
			Assert.assertEquals(new Integer(8), frame.line);
		}

		{
			GdbStackFrame frame = stackTrace.stack.get(1);
			Assert.assertEquals(new Integer(1), frame.level);
			Assert.assertEquals(new Long(0x0001076c), frame.address);
			Assert.assertEquals("callee3", frame.function);
			Assert.assertEquals("../../../devo/gdb/testsuite/gdb.mi/basics.c", frame.fileRelative);
			Assert.assertEquals("/home/foo/bar/devo/gdb/testsuite/gdb.mi/basics.c",
				frame.fileAbsolute);
			Assert.assertEquals(new Integer(17), frame.line);
		}
	}

	/**
	 * Verifies the correct conversion of a breakpoint message.
	 */
	@Test
	public void testBreakpoint() throws UnsupportedEncodingException
	{
		// Parse the message
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"^done," +
			"bkpt={" +
				"number=\"1\"," +
				"type=\"breakpoint\"," +
				"disp=\"keep\"," +
				"enabled=\"y\"," +
				"addr=\"0x000100d0\"," +
				"func=\"main\"," +
				"file=\"hello.c\"," +
				"fullname=\"/home/foo/hello.c\"," +
				"line=\"5\"," +
				"thread-groups=[\"i1\"]," +
				"times=\"0\"}\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));
		List<GdbMiRecord> records = parser.getRecords();

		// Convert the message
		GdbMiResultRecord record = (GdbMiResultRecord) records.get(0);
		Object object = GdbMiMessageConverter.processRecord(record, "-break-insert");
		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof GdbBreakpoint);

		GdbBreakpoint breakpoint = (GdbBreakpoint) object;
		Assert.assertEquals(new Integer(1), breakpoint.number);
		Assert.assertEquals(GdbBreakpoint.Type.Breakpoint, breakpoint.type);
		Assert.assertEquals(GdbBreakpoint.BreakpointDisposition.Keep, breakpoint.disposition);
		Assert.assertEquals(true, breakpoint.enabled);
		Assert.assertEquals(GdbBreakpoint.AddressAvailability.Available,
			breakpoint.addressAvailability);
		Assert.assertEquals(new Long(0x000100d0), breakpoint.address);
		Assert.assertEquals("main", breakpoint.function);
		Assert.assertEquals("hello.c", breakpoint.fileRelative);
		Assert.assertEquals("/home/foo/hello.c", breakpoint.fileAbsolute);
		Assert.assertEquals(new Integer(5), breakpoint.line);
		Assert.assertEquals(new Integer("0"), breakpoint.hitCount);

		Assert.assertEquals(1, breakpoint.threadGroups.size());
		Assert.assertEquals("i1", breakpoint.threadGroups.get(0));
	}
}
