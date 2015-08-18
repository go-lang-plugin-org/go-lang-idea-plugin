/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.debugger.gdb.gdbmi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Parser for GDB/MI output.
 */
public class GdbMiParser {
  // Possible states of the parser FSM
  private enum FsmState {
    Idle,                      // Ready to read a message
    Record,                    // Ready to read a record of any type
    ResultRecord,              // Reading a result record
    ResultRecordResults,       // Ready to read results from a result record
    ResultRecordResult,        // Reading a result from a result record
    ResultRecordResultEquals,  // Ready to read the '=' after a variable name
    ResultRecordResultValue,   // Ready to read a result value
    StreamRecord,              // Reading a stream output record
    String,                    // Reading a string
    StringEscape,              // Reading an escaped value from a string
    StringEscapeHex,           // Reading an escaped hexadecimal value from a string
    Tuple,                     // Reading a tuple
    TupleSeparator,            // Between results in a tuple
    TupleItem,                 // Ready to read a new item from a tuple
    List,                      // Reading a list
    ListValueSeparator,        // Between items in a list of values
    ListValueItem,             // Ready to read a new item from a list of values
    ListResultSeparator,       // Between items in a list of results
    ListResultItem,            // Ready to read a new item from a list of results
    StreamRecordSuffix,        // Ready to read a new line at the end of a stream record
    MessageSuffix              // Ready to read a new line at the end of a message
  }

  // State of the parser FSM
  private final Stack<FsmState> myState = new Stack<FsmState>();

  // Lexer
  private final GdbMiLexer myLexer = new GdbMiLexer();

  // Partially processed record
  @Nullable private GdbMiResultRecord myResultRecord;
  @Nullable private GdbMiStreamRecord myStreamRecord;
  @NotNull private final Stack<GdbMiValue> myValueStack = new Stack<GdbMiValue>();
  @Nullable private Long myRserToken;
  @Nullable private StringBuilder myBuilder;

  // List of unprocessed records
  @NotNull private final List<GdbMiRecord> myRecords = new ArrayList<GdbMiRecord>();

  /**
   * Constructor.
   */
  public GdbMiParser() {
    myState.push(FsmState.Idle);
  }

  /**
   * Returns a list of unprocessed records. The caller should erase items from this list as they
   * are processed.
   *
   * @return A list of unprocessed records.
   */
  @NotNull
  public List<GdbMiRecord> getRecords() {
    return myRecords;
  }

  /**
   * Processes the given data.
   *
   * @param data Data read from the GDB process.
   */
  public void process(@NotNull byte[] data) {
    process(data, data.length);
  }

  /**
   * Processes the given data.
   *
   * @param data   Data read from the GDB process.
   * @param length Number of bytes from data to process.
   */
  public void process(byte[] data, int length) {
    // Run the data through the lexer first
    myLexer.process(data, length);

    // Parse the data
    List<GdbMiToken> tokens = myLexer.getTokens();
    for (GdbMiToken token : tokens) {
      if (myState.isEmpty()) {
        throw new IllegalArgumentException("Mismatched tuple or list detected");
      }

      switch (myState.lastElement()) {
        case Idle:
          // Legal tokens:
          // UserToken
          // ResultRecordPrefix
          // ExecAsyncOutputPrefix
          // StatusAsyncOutputPrefix
          // NotifyAsyncOutputPrefix
          // ConsoleStreamOutputPrefix
          // TargetStreamOutputPrefix
          // LogStreamOutputPrefix
          // GdbSuffix
          switch (token.type) {
            case UserToken:
              myRserToken = Long.parseLong(token.value);
              setState(FsmState.Record);
              break;

            case ResultRecordPrefix:
              myResultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Immediate, myRserToken);
              myRserToken = null;
              setState(FsmState.ResultRecord);
              break;

            case StatusAsyncOutputPrefix:
              myResultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Status, myRserToken);
              myRserToken = null;
              setState(FsmState.ResultRecord);
              break;

            case ExecAsyncOutputPrefix:
              myResultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Exec, myRserToken);
              myRserToken = null;
              setState(FsmState.ResultRecord);
              break;

            case NotifyAsyncOutputPrefix:
              myResultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Notify, myRserToken);
              myRserToken = null;
              setState(FsmState.ResultRecord);
              break;

            case ConsoleStreamOutputPrefix:
              myStreamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Console, myRserToken);
              myRserToken = null;
              setState(FsmState.StreamRecord);
              break;

            case TargetStreamOutputPrefix:
              myStreamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Target, myRserToken);
              myRserToken = null;
              setState(FsmState.StreamRecord);
              break;

            case LogStreamOutputPrefix:
              myStreamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Log, myRserToken);
              myRserToken = null;
              setState(FsmState.StreamRecord);
              break;

            case GdbSuffix:
              setState(FsmState.MessageSuffix);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case Record:
          // Legal tokens:
          // ResultRecordPrefix
          // ExecAsyncOutputPrefix
          // StatusAsyncOutputPrefix
          // NotifyAsyncOutputPrefix
          // ConsoleStreamOutputPrefix
          // TargetStreamOutputPrefix
          // LogStreamOutputPrefix
          switch (token.type) {
            case ResultRecordPrefix:
              myResultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Immediate, myRserToken);
              myRserToken = null;
              setState(FsmState.ResultRecord);
              break;

            case StatusAsyncOutputPrefix:
              myResultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Status, myRserToken);
              myRserToken = null;
              setState(FsmState.ResultRecord);
              break;

            case ExecAsyncOutputPrefix:
              myResultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Exec, myRserToken);
              myRserToken = null;
              setState(FsmState.ResultRecord);
              break;

            case NotifyAsyncOutputPrefix:
              myResultRecord = new GdbMiResultRecord(GdbMiRecord.Type.Notify, myRserToken);
              myRserToken = null;
              setState(FsmState.ResultRecord);
              break;

            case ConsoleStreamOutputPrefix:
              myStreamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Console, myRserToken);
              myRserToken = null;
              setState(FsmState.StreamRecord);
              break;

            case TargetStreamOutputPrefix:
              myStreamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Target, myRserToken);
              myRserToken = null;
              setState(FsmState.StreamRecord);
              break;

            case LogStreamOutputPrefix:
              myStreamRecord = new GdbMiStreamRecord(GdbMiRecord.Type.Log, myRserToken);
              myRserToken = null;
              setState(FsmState.StreamRecord);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ResultRecord:
          // Legal tokens:
          // Identifier
          switch (token.type) {
            case Identifier:
              myResultRecord.className = token.value;
              setState(FsmState.ResultRecordResults);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ResultRecordResults:
          // Legal tokens:
          // ResultSeparator
          // NewLine
          switch (token.type) {
            case ResultSeparator:
              myState.push(FsmState.ResultRecordResult);
              break;

            case NewLine:
              myRecords.add(myResultRecord);
              myResultRecord = null;
              setState(FsmState.Idle);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ResultRecordResult:
          // Legal tokens:
          // Identifier
          switch (token.type) {
            case Identifier: {
              GdbMiResult result = new GdbMiResult(token.value);
              myValueStack.push(result.value);
              myResultRecord.results.add(result);
            }
            setState(FsmState.ResultRecordResultEquals);
            break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ResultRecordResultEquals:
          // Legal tokens:
          // Equals
          switch (token.type) {
            case Equals:
              setState(FsmState.ResultRecordResultValue);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ResultRecordResultValue:
          // Legal tokens:
          // StringPrefix
          // TuplePrefix
          // ListPrefix
          switch (token.type) {
            case StringPrefix:
              myValueStack.lastElement().type = GdbMiValue.Type.String;
              myBuilder = new StringBuilder();
              setState(FsmState.String);
              break;

            case TuplePrefix:
              myValueStack.lastElement().type = GdbMiValue.Type.Tuple;
              myValueStack.lastElement().tuple = new ArrayList<GdbMiResult>();
              setState(FsmState.Tuple);
              break;

            case ListPrefix:
              myValueStack.lastElement().type = GdbMiValue.Type.List;
              myValueStack.lastElement().list = new GdbMiList();
              setState(FsmState.List);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case StreamRecord:
          // Legal tokens:
          // StringPrefix
          switch (token.type) {
            case StringPrefix:
              myBuilder = new StringBuilder();
              setState(FsmState.String);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case String:
          // Legal tokens:
          // StringFragment
          // StringEscapePrefix
          // StringSuffix
          switch (token.type) {
            case StringFragment:
              myBuilder.append(token.value);
              break;

            case StringEscapePrefix:
              setState(FsmState.StringEscape);
              break;

            case StringSuffix:
              assert !myValueStack.isEmpty() || myStreamRecord != null;
              assert !(!myValueStack.isEmpty() && myStreamRecord != null);

              if (!myValueStack.isEmpty()) {
                // Currently reading a value
                GdbMiValue value = myValueStack.pop();
                assert value.type == GdbMiValue.Type.String;
                value.string = myBuilder.toString();
                myState.pop();
              }
              else {
                myStreamRecord.message = myBuilder.toString();
                setState(FsmState.StreamRecordSuffix);
              }
              myBuilder = null;
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case StringEscape:
          // Legal tokens:
          // StringEscapeApostrophe
          // StringEscapeQuote
          // StringEscapeQuestion
          // StringEscapeBackslash
          // StringEscapeAlarm
          // StringEscapeBackspace
          // StringEscapeFormFeed
          // StringEscapeNewLine
          // StringEscapeCarriageReturn
          // StringEscapeHorizontalTab
          // StringEscapeVerticalTab
          // StringEscapeHexPrefix
          // StringEscapeOctValue
          switch (token.type) {
            case StringEscapeApostrophe:
              myBuilder.append('\'');
              setState(FsmState.String);
              break;

            case StringEscapeQuote:
              myBuilder.append('"');
              setState(FsmState.String);
              break;

            case StringEscapeQuestion:
              myBuilder.append('?');
              setState(FsmState.String);
              break;

            case StringEscapeBackslash:
              myBuilder.append('\\');
              setState(FsmState.String);
              break;

            case StringEscapeAlarm:
              myBuilder.append('\u0007');
              setState(FsmState.String);
              break;

            case StringEscapeBackspace:
              myBuilder.append('\b');
              setState(FsmState.String);
              break;

            case StringEscapeFormFeed:
              myBuilder.append('\f');
              setState(FsmState.String);
              break;

            case StringEscapeNewLine:
              myBuilder.append('\n');
              setState(FsmState.String);
              break;

            case StringEscapeCarriageReturn:
              myBuilder.append('\r');
              setState(FsmState.String);
              break;

            case StringEscapeHorizontalTab:
              myBuilder.append('\t');
              setState(FsmState.String);
              break;

            case StringEscapeVerticalTab:
              myBuilder.append('\u000b');
              setState(FsmState.String);
              break;

            case StringEscapeHexPrefix:
              setState(FsmState.StringEscapeHex);
              break;

            case StringEscapeOctValue:
              // Octal values can be up to three characters long, which has a maximum value of
              // 0x1ff. As such, we need to parse it as an integer and then truncate it to
              // 8 bits before casting it to a 16-bit char to match the behaviour of C strings
            {
              int ch = Integer.parseInt(token.value, 8) & 0xff;
              myBuilder.append((char)ch);
            }
            setState(FsmState.String);
            break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case StringEscapeHex:
          // Legal tokens:
          // StringEscapeHexValue
          switch (token.type) {
            case StringEscapeHexValue:
              // Hex values are not limited in length, so we need to truncate it to the last
              // two characters to prevent Integer.parseInt from throwing an exception if it
              // is too long
            {
              int tokenLen = token.value.length();
              if (tokenLen > 2) {
                token.value = token.value.substring(tokenLen - 2, tokenLen);
              }
            }
            {
              int ch = Integer.parseInt(token.value, 16);
              myBuilder.append((char)ch);
            }
            setState(FsmState.String);
            break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case Tuple:
          // Legal tokens:
          // TupleSuffix
          // Identifier
          switch (token.type) {
            case TupleSuffix:
              myValueStack.pop();
              myState.pop();
              break;

            case Identifier: {
              GdbMiResult result = new GdbMiResult(token.value);
              myValueStack.lastElement().tuple.add(result);
              myValueStack.push(result.value);
            }
            myState.pop();
            myState.push(FsmState.TupleSeparator);
            myState.push(FsmState.ResultRecordResultEquals);
            break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case TupleSeparator:
          // Legal tokens:
          // TupleSuffix
          // ResultSeparator
          switch (token.type) {
            case TupleSuffix:
              myValueStack.pop();
              myState.pop();
              break;

            case ResultSeparator:
              setState(FsmState.TupleItem);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case TupleItem:
          // Legal tokens:
          // Identifier
          switch (token.type) {
            case Identifier: {
              GdbMiResult result = new GdbMiResult(token.value);
              myValueStack.lastElement().tuple.add(result);
              myValueStack.push(result.value);
            }
            myState.pop();
            myState.push(FsmState.TupleSeparator);
            myState.push(FsmState.ResultRecordResultEquals);
            break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case List:
          // Legal tokens:
          // ListSuffix
          // StringPrefix
          // TuplePrefix
          // ListPrefix
          // Identifier
          switch (token.type) {
            case ListSuffix:
              myValueStack.pop();
              myState.pop();
              break;

            case StringPrefix: {
              GdbMiList list = myValueStack.lastElement().list;
              list.type = GdbMiList.Type.Values;
              list.values = new ArrayList<GdbMiValue>();
              GdbMiValue value = new GdbMiValue(GdbMiValue.Type.String);
              list.values.add(value);
              myValueStack.push(value);
            }
            myState.pop();
            myState.push(FsmState.ListValueSeparator);
            myState.push(FsmState.String);
            myBuilder = new StringBuilder();
            break;

            case TuplePrefix: {
              GdbMiList list = myValueStack.lastElement().list;
              list.type = GdbMiList.Type.Values;
              list.values = new ArrayList<GdbMiValue>();
              GdbMiValue value = new GdbMiValue(GdbMiValue.Type.Tuple);
              value.tuple = new ArrayList<GdbMiResult>();
              list.values.add(value);
              myValueStack.push(value);
            }
            myState.pop();
            myState.push(FsmState.ListValueSeparator);
            myState.push(FsmState.Tuple);
            break;

            case Identifier: {
              GdbMiList list = myValueStack.lastElement().list;
              list.type = GdbMiList.Type.Results;
              list.results = new ArrayList<GdbMiResult>();
              GdbMiResult result = new GdbMiResult(token.value);
              list.results.add(result);
              myValueStack.push(result.value);
            }
            myState.pop();
            myState.push(FsmState.ListResultSeparator);
            myState.push(FsmState.ResultRecordResultEquals);
            break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ListValueSeparator:
          // Legal tokens:
          // ListSuffix
          // ResultSeparator
          switch (token.type) {
            case ListSuffix:
              myValueStack.pop();
              myState.pop();
              break;

            case ResultSeparator:
              setState(FsmState.ListValueItem);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ListValueItem:
          // Legal tokens:
          // StringPrefix
          // TuplePrefix
          // ListPrefix
          switch (token.type) {
            case StringPrefix: {
              GdbMiValue value = new GdbMiValue(GdbMiValue.Type.String);
              myValueStack.lastElement().list.values.add(value);
              myValueStack.push(value);
            }
            myState.pop();
            myState.push(FsmState.ListValueSeparator);
            myState.push(FsmState.String);
            myBuilder = new StringBuilder();
            break;

            case TuplePrefix: {
              GdbMiValue value = new GdbMiValue(GdbMiValue.Type.Tuple);
              value.tuple = new ArrayList<GdbMiResult>();
              myValueStack.lastElement().list.values.add(value);
              myValueStack.push(value);
            }
            myState.pop();
            myState.push(FsmState.ListValueSeparator);
            myState.push(FsmState.Tuple);
            break;

            case ListPrefix: {
              GdbMiValue value = new GdbMiValue(GdbMiValue.Type.List);
              value.list = new GdbMiList();
              myValueStack.lastElement().list.values.add(value);
              myValueStack.push(value);
            }
            myState.pop();
            myState.push(FsmState.ListValueSeparator);
            myState.push(FsmState.List);
            break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ListResultSeparator:
          // Legal tokens:
          // ListSuffix
          // ResultSeparator
          switch (token.type) {
            case ListSuffix:
              myValueStack.pop();
              myState.pop();
              break;

            case ResultSeparator:
              setState(FsmState.ListResultItem);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case ListResultItem:
          // Legal tokens:
          // Identifier
          switch (token.type) {
            case Identifier: {
              GdbMiList list = myValueStack.lastElement().list;
              GdbMiResult result = new GdbMiResult(token.value);
              list.results.add(result);
              myValueStack.push(result.value);
            }
            myState.pop();
            myState.push(FsmState.ListResultSeparator);
            myState.push(FsmState.ResultRecordResultEquals);
            break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case StreamRecordSuffix:
          // Legal tokens:
          // NewLine
          switch (token.type) {
            case NewLine:
              myRecords.add(myStreamRecord);
              myStreamRecord = null;
              setState(FsmState.Idle);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        case MessageSuffix:
          // Legal tokens:
          // NewLine
          switch (token.type) {
            case NewLine:
              setState(FsmState.Idle);
              break;

            default:
              throw new IllegalArgumentException("Unexpected token of type " + token.type);
          }
          break;

        default:
          throw new IllegalArgumentException("Unexpected parser FSM state: " +
                                             myState.lastElement());
      }
    }
    tokens.clear();
  }

  /**
   * Sets the state of the parser FSM.
   *
   * @param state The new state.
   */
  private void setState(FsmState state) {
    myState.pop();
    myState.push(state);
  }
}
