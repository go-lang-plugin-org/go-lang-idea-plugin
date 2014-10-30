package uk.co.cwspencer.gdb.gdbmi;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Tests for GdbMiParser2.
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
		GdbMiParser2 parser = new GdbMiParser2(null);
		String messageStr = "^error,msg=\"Undefined MI command: rubbish\"\r\n(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(1, records.size());

		GdbMiRecord record = records.get(0);
		Assert.assertNotNull(record);
		Assert.assertNull(record.userToken);
		Assert.assertEquals(GdbMiRecord.Type.Immediate, record.type);

		GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
		Assert.assertEquals("error", resultRecord.className);
		Assert.assertNotNull(resultRecord.results);
		Assert.assertEquals(1, resultRecord.results.size());

		GdbMiResult result = resultRecord.results.get(0);
		Assert.assertEquals("msg", result.variable);
		Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
		Assert.assertEquals("Undefined MI command: rubbish", result.value.string);
	}

	/**
	 * Tests the parsing of the message returned after setting a breakpoint.
	 */
	@Test
	@Ignore("not ready yet")
	public void testSetBreakpoint() throws UnsupportedEncodingException
	{
		// Input: -break-insert main
		GdbMiParser2 parser = new GdbMiParser2(null);
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
		Assert.assertEquals(1, records.size());

		GdbMiRecord record = records.get(0);
		Assert.assertEquals(GdbMiRecord.Type.Immediate, record.type);

		GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
		Assert.assertEquals("done", resultRecord.className);
		Assert.assertEquals(1, resultRecord.results.size());

		GdbMiResult result = resultRecord.results.get(0);
		Assert.assertEquals("bkpt", result.variable);
		Assert.assertEquals(GdbMiValue.Type.Tuple, result.value.type);
		Assert.assertEquals(11, result.value.tuple.size());

		// number="1"
		{
			GdbMiResult item = result.value.tuple.get(0);
			Assert.assertEquals("number", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("1", item.value.string);
		}

		// type="breakpoint"
		{
			GdbMiResult item = result.value.tuple.get(1);
			Assert.assertEquals("type", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("breakpoint", item.value.string);
		}

		// disp="keep"
		{
			GdbMiResult item = result.value.tuple.get(2);
			Assert.assertEquals("disp", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("keep", item.value.string);
		}

		// enabled="y"
		{
			GdbMiResult item = result.value.tuple.get(3);
			Assert.assertEquals("enabled", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("y", item.value.string);
		}

		// addr="0x08048564"
		{
			GdbMiResult item = result.value.tuple.get(4);
			Assert.assertEquals("addr", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("0x08048564", item.value.string);
		}

		// func="main"
		{
			GdbMiResult item = result.value.tuple.get(5);
			Assert.assertEquals("func", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("main", item.value.string);
		}

		// file="myprog.c"
		{
			GdbMiResult item = result.value.tuple.get(6);
			Assert.assertEquals("file", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("myprog.c", item.value.string);
		}

		// fullname="/home/nickrob/myprog.c"
		{
			GdbMiResult item = result.value.tuple.get(7);
			Assert.assertEquals("fullname", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("/home/nickrob/myprog.c", item.value.string);
		}

		// line="68"
		{
			GdbMiResult item = result.value.tuple.get(8);
			Assert.assertEquals("line", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("68", item.value.string);
		}

		// thread-groups=["i1"]
		{
			GdbMiResult item = result.value.tuple.get(9);
			Assert.assertEquals("thread-groups", item.variable);
			Assert.assertEquals(GdbMiValue.Type.List, item.value.type);
			Assert.assertEquals(GdbMiList.Type.Values, item.value.list.type);
			Assert.assertEquals(1, item.value.list.values.size());

			GdbMiValue value = item.value.list.values.get(0);
			Assert.assertEquals(GdbMiValue.Type.String, value.type);
			Assert.assertEquals("i1", value.string);
		}

		// times="0"
		{
			GdbMiResult item = result.value.tuple.get(10);
			Assert.assertEquals("times", item.variable);
			Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
			Assert.assertEquals("0", item.value.string);
		}
	}

	/**
	 * Tests the handling of messages from a typical execution sequence.
	 */
	@Test
	@Ignore("not ready yet")
	public void testExecution() throws UnsupportedEncodingException
	{
		// Input: -exec-run
		GdbMiParser2 parser = new GdbMiParser2(null);
		{
			String messageStr = "^running\r\n(gdb)\r\n";
			parser.process(messageStr.getBytes("US-ASCII"));

			List<GdbMiRecord> records = parser.getRecords();
			Assert.assertNotNull(records);
			Assert.assertEquals(1, records.size());

			GdbMiRecord record = records.get(0);
			Assert.assertEquals(GdbMiRecord.Type.Immediate, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("running", resultRecord.className);
			Assert.assertEquals(0, resultRecord.results.size());

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
			Assert.assertEquals(1, records.size());

			GdbMiRecord record = records.get(0);
			Assert.assertEquals(GdbMiRecord.Type.Exec, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("stopped", resultRecord.className);
			Assert.assertEquals(5, resultRecord.results.size());

			// reason="breakpoint-hit"
			{
				GdbMiResult result = resultRecord.results.get(0);
				Assert.assertEquals("reason", result.variable);
				Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
				Assert.assertEquals("breakpoint-hit", result.value.string);
			}

			// disp="keep"
			{
				GdbMiResult result = resultRecord.results.get(1);
				Assert.assertEquals("disp", result.variable);
				Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
				Assert.assertEquals("keep", result.value.string);
			}

			// bkptno="1"
			{
				GdbMiResult result = resultRecord.results.get(2);
				Assert.assertEquals("bkptno", result.variable);
				Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
				Assert.assertEquals("1", result.value.string);
			}

			// thread-id="0"
			{
				GdbMiResult result = resultRecord.results.get(3);
				Assert.assertEquals("thread-id", result.variable);
				Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
				Assert.assertEquals("0", result.value.string);
			}

			// frame={...}
			{
				GdbMiResult frame = resultRecord.results.get(4);
				Assert.assertEquals("frame", frame.variable);
				Assert.assertEquals(GdbMiValue.Type.Tuple, frame.value.type);
				Assert.assertEquals(6, frame.value.tuple.size());

				// addr="0x08048564"
				{
					GdbMiResult result = frame.value.tuple.get(0);
					Assert.assertEquals("addr", result.variable);
					Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
					Assert.assertEquals("0x08048564", result.value.string);
				}

				// func="main"
				{
					GdbMiResult result = frame.value.tuple.get(1);
					Assert.assertEquals("func", result.variable);
					Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
					Assert.assertEquals("main", result.value.string);
				}

				// args=[...]
				{
					GdbMiResult result = frame.value.tuple.get(2);
					Assert.assertEquals("args", result.variable);
					Assert.assertEquals(GdbMiValue.Type.List, result.value.type);
					Assert.assertEquals(GdbMiList.Type.Values, result.value.list.type);
					Assert.assertEquals(2, result.value.list.values.size());

					// {name="argc",value="1"}
					{
						GdbMiValue value = result.value.list.values.get(0);
						Assert.assertEquals(GdbMiValue.Type.Tuple, value.type);
						Assert.assertEquals(2, value.tuple.size());

						{
							GdbMiResult tupleItem = value.tuple.get(0);
							Assert.assertEquals("name", tupleItem.variable);
							Assert.assertEquals(GdbMiValue.Type.String, tupleItem.value.type);
							Assert.assertEquals("argc", tupleItem.value.string);
						}

						{
							GdbMiResult tupleItem = value.tuple.get(1);
							Assert.assertEquals("value", tupleItem.variable);
							Assert.assertEquals(GdbMiValue.Type.String, tupleItem.value.type);
							Assert.assertEquals("1", tupleItem.value.string);
						}
					}

					// {name="argv",value="0xbfc4d4d4"}
					{
						GdbMiValue value = result.value.list.values.get(1);
						Assert.assertEquals(GdbMiValue.Type.Tuple, value.type);
						Assert.assertEquals(2, value.tuple.size());

						{
							GdbMiResult tupleItem = value.tuple.get(0);
							Assert.assertEquals("name", tupleItem.variable);
							Assert.assertEquals(GdbMiValue.Type.String, tupleItem.value.type);
							Assert.assertEquals("argv", tupleItem.value.string);
						}

						{
							GdbMiResult tupleItem = value.tuple.get(1);
							Assert.assertEquals("value", tupleItem.variable);
							Assert.assertEquals(GdbMiValue.Type.String, tupleItem.value.type);
							Assert.assertEquals("0xbfc4d4d4", tupleItem.value.string);
						}
					}
				}

				// file="myprog.c"
				{
					GdbMiResult result = frame.value.tuple.get(3);
					Assert.assertEquals("file", result.variable);
					Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
					Assert.assertEquals("myprog.c", result.value.string);
				}

				// fullname="/home/nickrob/myprog.c"
				{
					GdbMiResult result = frame.value.tuple.get(4);
					Assert.assertEquals("fullname", result.variable);
					Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
					Assert.assertEquals("/home/nickrob/myprog.c", result.value.string);
				}

				// line="68"
				{
					GdbMiResult result = frame.value.tuple.get(5);
					Assert.assertEquals("line", result.variable);
					Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
					Assert.assertEquals("68", result.value.string);
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
			Assert.assertEquals(1, records.size());

			GdbMiRecord record = records.get(0);
			Assert.assertEquals(GdbMiRecord.Type.Immediate, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("running", resultRecord.className);
			Assert.assertEquals(0, resultRecord.results.size());

			records.clear();
		}

		// Later...
		{
			String messageStr = "*stopped,reason=\"exited-normally\"\r\n(gdb)\r\n";
			parser.process(messageStr.getBytes("US-ASCII"));

			List<GdbMiRecord> records = parser.getRecords();
			Assert.assertNotNull(records);
			Assert.assertEquals(1, records.size());

			GdbMiRecord record = records.get(0);
			Assert.assertEquals(GdbMiRecord.Type.Exec, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("stopped", resultRecord.className);
			Assert.assertEquals(1, resultRecord.results.size());

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals("reason", result.variable);
			Assert.assertEquals(GdbMiValue.Type.String, result.value.type);
			Assert.assertEquals("exited-normally", result.value.string);

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
		GdbMiParser2 parser = new GdbMiParser2(null);
		String messageStr = "12345^done\r\n(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(1, records.size());

		GdbMiRecord record = records.get(0);
		Assert.assertNotNull(record);
		Assert.assertEquals(new Long(12345), record.userToken);
		Assert.assertEquals(GdbMiRecord.Type.Immediate, record.type);

		GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
		Assert.assertEquals("done", resultRecord.className);
		Assert.assertNotNull(resultRecord.results);
		Assert.assertEquals(0, resultRecord.results.size());
	}

	/**
	 * Verifies escape sequences in strings are handled correctly.
	 */
	@Test
	@Ignore("not ready yet")
	public void testStringEscape() throws UnsupportedEncodingException
	{
		GdbMiParser2 parser = new GdbMiParser2(null);
		String messageStr =
			"~\">>\\a\\b\\f\\n\\r\\t\\v\\'||\\\"\\\\\\?\\5\\66\\777\\38\\xag\\xaf\\xbcd<<\"\r\n" +
			"(gdb)\r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(1, records.size());

		GdbMiRecord record = records.get(0);
		Assert.assertNotNull(record);
		Assert.assertNull(record.userToken);
		Assert.assertEquals(GdbMiRecord.Type.Console, record.type);

		GdbMiStreamRecord streamRecord = (GdbMiStreamRecord) record;

		String expected = ">>\u0007\b\f\n\r\t\u000b\'||\"\\?\u00056\u00ff\u00038\ng\u00af\u00cd<<";
		Assert.assertEquals(expected, streamRecord.message);
	}

	/**
	 * Verifies single CR line breaks. are handled the same as normal CRLF line breaks.
	 */
	@Test
	public void testCr() throws UnsupportedEncodingException
	{
		GdbMiParser2 parser = new GdbMiParser2(null);
		String messageStr =
			"~\"foo\"\r" +
			"~\"bar\"\r\n" +
			"(gdb)\r";
		parser.process(messageStr.getBytes("US-ASCII"));

		List<GdbMiRecord> records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(2, records.size());

		{
			GdbMiRecord record = records.get(0);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(GdbMiRecord.Type.Console, record.type);

			GdbMiStreamRecord streamRecord = (GdbMiStreamRecord) record;
			Assert.assertEquals("foo", streamRecord.message);
		}

		{
			GdbMiRecord record = records.get(1);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(GdbMiRecord.Type.Console, record.type);

			GdbMiStreamRecord streamRecord = (GdbMiStreamRecord) record;
			Assert.assertEquals("bar", streamRecord.message);
		}
	}

	/**
	 * Tests the correct handling of tuples and lists.
	 */
	@Test(expected = AssertionError.class)
	public void testTuplesAndLists() throws UnsupportedEncodingException
	{
		GdbMiParser2 parser = new GdbMiParser2(null);
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
		Assert.assertEquals(5, records.size());

		// *stopped,test={}
		{
			GdbMiRecord record = records.get(0);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(GdbMiRecord.Type.Exec, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("stopped", resultRecord.className);
			Assert.assertEquals(1, resultRecord.results.size());

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals("test", result.variable);
			Assert.assertEquals(GdbMiValue.Type.Tuple, result.value.type);
			Assert.assertEquals(0, result.value.tuple.size());
		}

		// *stopped,test=[]
		{
			GdbMiRecord record = records.get(1);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(GdbMiRecord.Type.Exec, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("stopped", resultRecord.className);
			Assert.assertEquals(1, resultRecord.results.size());

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals("test", result.variable);
			Assert.assertEquals(GdbMiValue.Type.List, result.value.type);
			Assert.assertEquals(GdbMiList.Type.Empty, result.value.list.type);
			Assert.assertNull(result.value.list.values);
			Assert.assertNull(result.value.list.results);
		}

		// *stopped,test={foo=[],bar="baz"}
		{
			GdbMiRecord record = records.get(2);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(GdbMiRecord.Type.Exec, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("stopped", resultRecord.className);
			Assert.assertEquals(1, resultRecord.results.size());

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals("test", result.variable);
			Assert.assertEquals(GdbMiValue.Type.Tuple, result.value.type);
			Assert.assertEquals(2, result.value.tuple.size());

			{
				GdbMiResult item = result.value.tuple.get(0);
				Assert.assertEquals("foo", item.variable);
				Assert.assertEquals(GdbMiValue.Type.List, item.value.type);
				Assert.assertEquals(GdbMiList.Type.Empty, item.value.list.type);
				Assert.assertNull(item.value.list.values);
				Assert.assertNull(item.value.list.results);
			}

			{
				GdbMiResult item = result.value.tuple.get(1);
				Assert.assertEquals("bar", item.variable);
				Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
				Assert.assertEquals("baz", item.value.string);
			}
		}

		// *stopped,test=[{},"foo",[blar="fred"]]
		{
			GdbMiRecord record = records.get(3);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(GdbMiRecord.Type.Exec, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("stopped", resultRecord.className);
			Assert.assertEquals(1, resultRecord.results.size());

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals("test", result.variable);
			Assert.assertEquals(GdbMiValue.Type.List, result.value.type);
			Assert.assertEquals(GdbMiList.Type.Values, result.value.list.type);
			Assert.assertNull(result.value.list.results);
			Assert.assertEquals(3, result.value.list.values.size());

			{
				GdbMiValue item = result.value.list.values.get(0);
				Assert.assertEquals(GdbMiValue.Type.Tuple, item.type);
				Assert.assertEquals(0, item.tuple.size());
			}

			{
				GdbMiValue item = result.value.list.values.get(1);
				Assert.assertEquals(GdbMiValue.Type.String, item.type);
				Assert.assertEquals("foo", item.string);
			}

			{
				GdbMiValue item = result.value.list.values.get(2);
				Assert.assertEquals(GdbMiValue.Type.List, item.type);
				Assert.assertEquals(GdbMiList.Type.Results, item.list.type);
				Assert.assertNull(item.list.values);
				Assert.assertEquals(1, item.list.results.size());

				GdbMiResult blar = item.list.results.get(0);
				Assert.assertEquals("blar", blar.variable);
				Assert.assertEquals(GdbMiValue.Type.String, blar.value.type);
				Assert.assertEquals("fred", blar.value.string);
			}
		}

		// *stopped,test=[foo={},bar="baz"]
		{
			GdbMiRecord record = records.get(4);
			Assert.assertNotNull(record);
			Assert.assertNull(record.userToken);
			Assert.assertEquals(GdbMiRecord.Type.Exec, record.type);

			GdbMiResultRecord resultRecord = (GdbMiResultRecord) record;
			Assert.assertEquals("stopped", resultRecord.className);
			Assert.assertEquals(1, resultRecord.results.size());

			GdbMiResult result = resultRecord.results.get(0);
			Assert.assertEquals("test", result.variable);
			Assert.assertEquals(GdbMiValue.Type.List, result.value.type);
			Assert.assertEquals(GdbMiList.Type.Results, result.value.list.type);
			Assert.assertNull(result.value.list.values);
			Assert.assertEquals(2, result.value.list.results.size());

			{
				GdbMiResult item = result.value.list.results.get(0);
				Assert.assertEquals("foo", item.variable);
				Assert.assertEquals(GdbMiValue.Type.Tuple, item.value.type);
				Assert.assertEquals(0, item.value.tuple.size());
			}

			{
				GdbMiResult item = result.value.list.results.get(1);
				Assert.assertEquals("bar", item.variable);
				Assert.assertEquals(GdbMiValue.Type.String, item.value.type);
				Assert.assertEquals("baz", item.value.string);
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
		GdbMiParser2 parser = new GdbMiParser2(null);
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
		Assert.assertEquals(1, records.size());
		records.clear();

		// Later...
		messageStr =
			"23^error,msg=\"Undefined MI command: blar\"\r\n" +
			"(gdb) \r\n";
		parser.process(messageStr.getBytes("US-ASCII"));

		records = parser.getRecords();
		Assert.assertNotNull(records);
		Assert.assertEquals(1, records.size());
		records.clear();
	}
}
