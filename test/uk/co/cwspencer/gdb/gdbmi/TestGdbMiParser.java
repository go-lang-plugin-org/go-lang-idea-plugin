package uk.co.cwspencer.gdb.gdbmi;

import org.junit.Assert;
import org.junit.Test;
import uk.co.cwspencer.gdb.gdbmi.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Tests for GdbMiParser.
 */
public class TestGdbMiParser
{
	/**
	 * Tests the parsing of the message returned after sending a bad command.
	 */
	@Test
	public void testBadCommand() throws UnsupportedEncodingException
	{
		// Input: -rubbish
		GdbMiParser parser = new GdbMiParser();
		String messageStr = "^error,msg=\"Undefined MI command: rubbish\"\r\n(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(records.size(), 1);

		GdbMiRecord record = records.get(0);
		Assert.assertNotNull(record);
		Assert.assertNull(record.userToken);
		Assert.assertEquals(record.type, GdbMiRecord.Type.Immediate);

		GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
		Assert.assertEquals(resultRecord.className, "error");
		Assert.assertNotNull(resultRecord.results);
		Assert.assertEquals(resultRecord.results.size(), 1);

		GdbMiResult result = resultRecord.results.get(0);
		Assert.assertEquals(result.variable, "msg");
		Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
		Assert.assertEquals(result.value.string, "Undefined MI command: rubbish");
	}

	/**
	 * Tests the parsing of the message returned after setting a breakpoint.
	 */
	@Test
	public void testSetBreakpoint() throws UnsupportedEncodingException
	{
		// Input: -break-insert main
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"^done," +
			"bkpt={" +
				"number=\"1\"," +
				"type=\"breakpoint\"," +
				"disp=\"keep\"," +
				"enabled=\"y\"," +
				"addr=\"0x08048564\"," +
				"func=\"main\"," +
				"file=\"myprog.c\"," +
				"fullname=\"/home/nickrob/myprog.c\"," +
				"line=\"68\"," +
				"thread-groups=[\"i1\"]," +
				"times=\"0\"}\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertEquals(records.size(), 1);

		GdbMiRecord record = records.get(0);
		Assert.assertEquals(record.type, GdbMiRecord.Type.Immediate);

		GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
		Assert.assertEquals(resultRecord.className, "done");
		Assert.assertEquals(resultRecord.results.size(), 1);

		GdbMiResult result = resultRecord.results.get(0);
		Assert.assertEquals(result.variable, "bkpt");
		Assert.assertEquals(result.value.type, GdbMiValue.Type.Tuple);
		Assert.assertEquals(result.value.tuple.size(), 11);

		// number="1"
		{
			GdbMiResult item = result.value.tuple.get(0);
			Assert.assertEquals(item.variable, "number");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "1");
		}

		// type="breakpoint"
		{
			GdbMiResult item = result.value.tuple.get(1);
			Assert.assertEquals(item.variable, "type");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "breakpoint");
		}

		// disp="keep"
		{
			GdbMiResult item = result.value.tuple.get(2);
			Assert.assertEquals(item.variable, "disp");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "keep");
		}

		// enabled="y"
		{
			GdbMiResult item = result.value.tuple.get(3);
			Assert.assertEquals(item.variable, "enabled");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "y");
		}

		// addr="0x08048564"
		{
			GdbMiResult item = result.value.tuple.get(4);
			Assert.assertEquals(item.variable, "addr");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "0x08048564");
		}

		// func="main"
		{
			GdbMiResult item = result.value.tuple.get(5);
			Assert.assertEquals(item.variable, "func");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "main");
		}

		// file="myprog.c"
		{
			GdbMiResult item = result.value.tuple.get(6);
			Assert.assertEquals(item.variable, "file");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "myprog.c");
		}

		// fullname="/home/nickrob/myprog.c"
		{
			GdbMiResult item = result.value.tuple.get(7);
			Assert.assertEquals(item.variable, "fullname");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "/home/nickrob/myprog.c");
		}

		// line="68"
		{
			GdbMiResult item = result.value.tuple.get(8);
			Assert.assertEquals(item.variable, "line");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "68");
		}

		// thread-groups=["i1"]
		{
			GdbMiResult item = result.value.tuple.get(9);
			Assert.assertEquals(item.variable, "thread-groups");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.List);
			Assert.assertEquals(item.value.list.type, GdbMiList.Type.Values);
			Assert.assertEquals(item.value.list.values.size(), 1);

			GdbMiValue value = item.value.list.values.get(0);
			Assert.assertEquals(value.type, GdbMiValue.Type.String);
			Assert.assertEquals(value.string, "i1");
		}

		// times="0"
		{
			GdbMiResult item = result.value.tuple.get(10);
			Assert.assertEquals(item.variable, "times");
			Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(item.value.string, "0");
		}
	}

	/**
	 * Tests the handling of messages from a typical execution sequence.
	 */
	@Test
	public void testExecution() throws UnsupportedEncodingException
	{
		// Input: -exec-run
		GdbMiParser parser = new GdbMiParser();
		{
			String messageStr = "^running\r\n(gdb)\r\n";
			parser.process(messageStr.getBytes("US-ASCII"));

			List<GdbMiRecord> records = parser.getRecords();
			Assert.assertNotNull(records);
			Assert.assertEquals(records.size(), 1);

			GdbMiRecord record = records.get(0);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Immediate);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "running");
			Assert.assertEquals(resultRecord.results.size(), 0);

			records.clear();
		}

		// Later...
		{
			String messageStr =
				"*stopped," +
				"reason=\"breakpoint-hit\"," +
				"disp=\"keep\"," +
				"bkptno=\"1\"," +
				"thread-id=\"0\"," +
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
				"(gdb)\r\n";
			parser.process(messageStr.getBytes("US-ASCII"));

			List<GdbMiRecord> records = parser.getRecords();
			Assert.assertNotNull(records);
			Assert.assertEquals(records.size(), 1);

			GdbMiRecord record = records.get(0);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Exec);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "stopped");
			Assert.assertEquals(resultRecord.results.size(), 5);

			// reason="breakpoint-hit"
			{
				GdbMiResult result = resultRecord.results.get(0);
				Assert.assertEquals(result.variable, "reason");
				Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
				Assert.assertEquals(result.value.string, "breakpoint-hit");
			}

			// disp="keep"
			{
				GdbMiResult result = resultRecord.results.get(1);
				Assert.assertEquals(result.variable, "disp");
				Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
				Assert.assertEquals(result.value.string, "keep");
			}

			// bkptno="1"
			{
				GdbMiResult result = resultRecord.results.get(2);
				Assert.assertEquals(result.variable, "bkptno");
				Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
				Assert.assertEquals(result.value.string, "1");
			}

			// thread-id="0"
			{
				GdbMiResult result = resultRecord.results.get(3);
				Assert.assertEquals(result.variable, "thread-id");
				Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
				Assert.assertEquals(result.value.string, "0");
			}

			// frame={...}
			{
				GdbMiResult frame = resultRecord.results.get(4);
				Assert.assertEquals(frame.variable, "frame");
				Assert.assertEquals(frame.value.type, GdbMiValue.Type.Tuple);
				Assert.assertEquals(frame.value.tuple.size(), 6);

				// addr="0x08048564"
				{
					GdbMiResult result = frame.value.tuple.get(0);
					Assert.assertEquals(result.variable, "addr");
					Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
					Assert.assertEquals(result.value.string, "0x08048564");
				}

				// func="main"
				{
					GdbMiResult result = frame.value.tuple.get(1);
					Assert.assertEquals(result.variable, "func");
					Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
					Assert.assertEquals(result.value.string, "main");
				}

				// args=[...]
				{
					GdbMiResult result = frame.value.tuple.get(2);
					Assert.assertEquals(result.variable, "args");
					Assert.assertEquals(result.value.type, GdbMiValue.Type.List);
					Assert.assertEquals(result.value.list.type, GdbMiList.Type.Values);
					Assert.assertEquals(result.value.list.values.size(), 2);

					// {name="argc",value="1"}
					{
						GdbMiValue value = result.value.list.values.get(0);
						Assert.assertEquals(value.type, GdbMiValue.Type.Tuple);
						Assert.assertEquals(value.tuple.size(), 2);

						{
							GdbMiResult tupleItem = value.tuple.get(0);
							Assert.assertEquals(tupleItem.variable, "name");
							Assert.assertEquals(tupleItem.value.type, GdbMiValue.Type.String);
							Assert.assertEquals(tupleItem.value.string, "argc");
						}

						{
							GdbMiResult tupleItem = value.tuple.get(1);
							Assert.assertEquals(tupleItem.variable, "value");
							Assert.assertEquals(tupleItem.value.type, GdbMiValue.Type.String);
							Assert.assertEquals(tupleItem.value.string, "1");
						}
					}

					// {name="argv",value="0xbfc4d4d4"}
					{
						GdbMiValue value = result.value.list.values.get(1);
						Assert.assertEquals(value.type, GdbMiValue.Type.Tuple);
						Assert.assertEquals(value.tuple.size(), 2);

						{
							GdbMiResult tupleItem = value.tuple.get(0);
							Assert.assertEquals(tupleItem.variable, "name");
							Assert.assertEquals(tupleItem.value.type, GdbMiValue.Type.String);
							Assert.assertEquals(tupleItem.value.string, "argv");
						}

						{
							GdbMiResult tupleItem = value.tuple.get(1);
							Assert.assertEquals(tupleItem.variable, "value");
							Assert.assertEquals(tupleItem.value.type, GdbMiValue.Type.String);
							Assert.assertEquals(tupleItem.value.string, "0xbfc4d4d4");
						}
					}
				}

				// file="myprog.c"
				{
					GdbMiResult result = frame.value.tuple.get(3);
					Assert.assertEquals(result.variable, "file");
					Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
					Assert.assertEquals(result.value.string, "myprog.c");
				}

				// fullname="/home/nickrob/myprog.c"
				{
					GdbMiResult result = frame.value.tuple.get(4);
					Assert.assertEquals(result.variable, "fullname");
					Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
					Assert.assertEquals(result.value.string, "/home/nickrob/myprog.c");
				}

				// line="68"
				{
					GdbMiResult result = frame.value.tuple.get(5);
					Assert.assertEquals(result.variable, "line");
					Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
					Assert.assertEquals(result.value.string, "68");
				}
			}

			records.clear();
		}

		// Input: -exec-continue
		{
			String messageStr = "^running\r\n(gdb)\r\n";
			parser.process(messageStr.getBytes("US-ASCII"));

			List<GdbMiRecord> records = parser.getRecords();
			Assert.assertNotNull(records);
			Assert.assertEquals(records.size(), 1);

			GdbMiRecord record = records.get(0);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Immediate);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "running");
			Assert.assertEquals(resultRecord.results.size(), 0);

			records.clear();
		}

		// Later...
		{
			String messageStr = "*stopped,reason=\"exited-normally\"\r\n(gdb)\r\n";
			parser.process(messageStr.getBytes("US-ASCII"));

			List<GdbMiRecord> records = parser.getRecords();
			Assert.assertNotNull(records);
			Assert.assertEquals(records.size(), 1);

			GdbMiRecord record = records.get(0);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Exec);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "stopped");
			Assert.assertEquals(resultRecord.results.size(), 1);

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals(result.variable, "reason");
			Assert.assertEquals(result.value.type, GdbMiValue.Type.String);
			Assert.assertEquals(result.value.string, "exited-normally");

			records.clear();
		}
	}

	/**
	 * Verifies the token in a message is recorded correctly.
	 */
	@Test
	public void testToken() throws UnsupportedEncodingException
	{
		// Input: 12345print 1+2
		GdbMiParser parser = new GdbMiParser();
		String messageStr = "12345^done\r\n(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(records.size(), 1);

		GdbMiRecord record = records.get(0);
		Assert.assertNotNull(record);
		Assert.assertEquals(record.userToken, new Long(12345));
		Assert.assertEquals(record.type, GdbMiRecord.Type.Immediate);

		GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
		Assert.assertEquals(resultRecord.className, "done");
		Assert.assertNotNull(resultRecord.results);
		Assert.assertEquals(resultRecord.results.size(), 0);
	}

	/**
	 * Verifies escape sequences in strings are handled correctly.
	 */
	@Test
	public void testStringEscape() throws UnsupportedEncodingException
	{
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"~\">>\\a\\b\\f\\n\\r\\t\\v\\'||\\\"\\\\\\?\\5\\66\\777\\38\\xag\\xaf\\xbcd<<\"\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(records.size(), 1);

		GdbMiRecord record = records.get(0);
		Assert.assertNotNull(record);
		Assert.assertNull(record.userToken);
		Assert.assertEquals(record.type, GdbMiRecord.Type.Console);

		GdbMiStreamRecord streamRecord = (GdbMiStreamRecord) record;

		String expected = ">>\u0007\b\f\n\r\t\u000b\'||\"\\?\u00056\u00ff\u00038\ng\u00af\u00cd<<";
		Assert.assertEquals(streamRecord.message, expected);
	}

	/**
	 * Verifies single CR line breaks. are handled the same as normal CRLF line breaks.
	 */
	@Test
	public void testCr() throws UnsupportedEncodingException
	{
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"~\"foo\"\r" +
			"~\"bar\"\r\n" +
			"(gdb)\r";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(records.size(), 2);

		{
			GdbMiRecord record = records.get(0);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Console);

			GdbMiStreamRecord streamRecord = (GdbMiStreamRecord) record;
			Assert.assertEquals(streamRecord.message, "foo");
		}

		{
			GdbMiRecord record = records.get(1);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Console);

			GdbMiStreamRecord streamRecord = (GdbMiStreamRecord) record;
			Assert.assertEquals(streamRecord.message, "bar");
		}
	}

	/**
	 * Tests the correct handling of tuples and lists.
	 */
	@Test
	public void testTuplesAndLists() throws UnsupportedEncodingException
	{
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"*stopped,test={}\r\n" +
			"*stopped,test=[]\r\n" +
			"*stopped,test={foo=[],bar=\"baz\"}\r\n" +
			"*stopped,test=[{},\"foo\",[blar=\"fred\"]]\r\n" +
			"*stopped,test=[foo={},bar=\"baz\"]\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(records.size(), 5);

		// *stopped,test={}
		{
			GdbMiRecord record = records.get(0);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Exec);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "stopped");
			Assert.assertEquals(resultRecord.results.size(), 1);

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals(result.variable, "test");
			Assert.assertEquals(result.value.type, GdbMiValue.Type.Tuple);
			Assert.assertEquals(result.value.tuple.size(), 0);
		}

		// *stopped,test=[]
		{
			GdbMiRecord record = records.get(1);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Exec);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "stopped");
			Assert.assertEquals(resultRecord.results.size(), 1);

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals(result.variable, "test");
			Assert.assertEquals(result.value.type, GdbMiValue.Type.List);
			Assert.assertEquals(result.value.list.type, GdbMiList.Type.Empty);
			Assert.assertNull(result.value.list.values);
			Assert.assertNull(result.value.list.results);
		}

		// *stopped,test={foo=[],bar="baz"}
		{
			GdbMiRecord record = records.get(2);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Exec);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "stopped");
			Assert.assertEquals(resultRecord.results.size(), 1);

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals(result.variable, "test");
			Assert.assertEquals(result.value.type, GdbMiValue.Type.Tuple);
			Assert.assertEquals(result.value.tuple.size(), 2);

			{
				GdbMiResult item = result.value.tuple.get(0);
				Assert.assertEquals(item.variable, "foo");
				Assert.assertEquals(item.value.type, GdbMiValue.Type.List);
				Assert.assertEquals(item.value.list.type, GdbMiList.Type.Empty);
				Assert.assertNull(item.value.list.values);
				Assert.assertNull(item.value.list.results);
			}

			{
				GdbMiResult item = result.value.tuple.get(1);
				Assert.assertEquals(item.variable, "bar");
				Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
				Assert.assertEquals(item.value.string, "baz");
			}
		}

		// *stopped,test=[{},"foo",[blar="fred"]]
		{
			GdbMiRecord record = records.get(3);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Exec);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "stopped");
			Assert.assertEquals(resultRecord.results.size(), 1);

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals(result.variable, "test");
			Assert.assertEquals(result.value.type, GdbMiValue.Type.List);
			Assert.assertEquals(result.value.list.type, GdbMiList.Type.Values);
			Assert.assertNull(result.value.list.results);
			Assert.assertEquals(result.value.list.values.size(), 3);

			{
				GdbMiValue item = result.value.list.values.get(0);
				Assert.assertEquals(item.type, GdbMiValue.Type.Tuple);
				Assert.assertEquals(item.tuple.size(), 0);
			}

			{
				GdbMiValue item = result.value.list.values.get(1);
				Assert.assertEquals(item.type, GdbMiValue.Type.String);
				Assert.assertEquals(item.string, "foo");
			}

			{
				GdbMiValue item = result.value.list.values.get(2);
				Assert.assertEquals(item.type, GdbMiValue.Type.List);
				Assert.assertEquals(item.list.type, GdbMiList.Type.Results);
				Assert.assertNull(item.list.values);
				Assert.assertEquals(item.list.results.size(), 1);

				GdbMiResult blar = item.list.results.get(0);
				Assert.assertEquals(blar.variable, "blar");
				Assert.assertEquals(blar.value.type, GdbMiValue.Type.String);
				Assert.assertEquals(blar.value.string, "fred");
			}
		}

		// *stopped,test=[foo={},bar="baz"]
		{
			GdbMiRecord record = records.get(4);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(record.type, GdbMiRecord.Type.Exec);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals(resultRecord.className, "stopped");
			Assert.assertEquals(resultRecord.results.size(), 1);

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals(result.variable, "test");
			Assert.assertEquals(result.value.type, GdbMiValue.Type.List);
			Assert.assertEquals(result.value.list.type, GdbMiList.Type.Results);
			Assert.assertNull(result.value.list.values);
			Assert.assertEquals(result.value.list.results.size(), 2);

			{
				GdbMiResult item = result.value.list.results.get(0);
				Assert.assertEquals(item.variable, "foo");
				Assert.assertEquals(item.value.type, GdbMiValue.Type.Tuple);
				Assert.assertEquals(item.value.tuple.size(), 0);
			}

			{
				GdbMiResult item = result.value.list.results.get(1);
				Assert.assertEquals(item.variable, "bar");
				Assert.assertEquals(item.value.type, GdbMiValue.Type.String);
				Assert.assertEquals(item.value.string, "baz");
			}
		}
	}

	/**
	 * Tests the correct handling of messages when there is no "(gdb)" suffix. This can happen in
	 * asynchronous execution mode.
	 */
	@Test
	public void testMissingGdbSuffix() throws UnsupportedEncodingException
	{
		GdbMiParser parser = new GdbMiParser();
		String messageStr =
			"*stopped," +
			"reason=\"breakpoint-hit\"," +
			"disp=\"keep\"," +
			"bkptno=\"1\"," +
			"frame={" +
				"addr=\"0xadbde4e3\"," +
				"func=\"Java_uk_co_cwspencer_ideandktest_IdeaNdkTestJni_doSomething\"," +
				"args=[]," +
				"file=\"E:/Projects/Android/IdeaNdkTest/jni/idea_ndk_test_jni.cpp\"," +
				"fullname=\"E:/Projects/Android/IdeaNdkTest/jni/idea_ndk_test_jni.cpp\"," +
				"line=\"5\"}," +
			"thread-id=\"1\"," +
			"stopped-threads=\"all\"\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(records.size(), 1);
		records.clear();

		// Later...
		messageStr =
			"23^error,msg=\"Undefined MI command: blar\"\r\n" +
			"(gdb) \r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(records.size(), 1);
		records.clear();
	}
}
