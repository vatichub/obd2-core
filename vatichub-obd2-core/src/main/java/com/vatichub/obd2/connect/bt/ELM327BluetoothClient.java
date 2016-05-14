package com.vatichub.obd2.connect.bt;

import android.bluetooth.BluetoothSocket;
import android.nfc.FormatException;
import android.os.Handler;
import android.util.Log;

import com.vatichub.obd2.OBD2CoreConstants;
import com.vatichub.obd2.OBD2EventManager;
import com.vatichub.obd2.util.OBD2CoreUtils;
import com.vatichub.obd2.fileio.AppFileLogger;
import com.vatichub.obd2.OBD2CoreConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

import expr.Expr;
import expr.Parser;
import expr.SyntaxException;
import expr.Variable;

/**
    * This thread runs during a connection with a remote device.
    * It handles all incoming and outgoing transmissions.
    */
   public class ELM327BluetoothClient extends Thread implements BluetoothClient {

       // Debugging
       private static final String TAG = OBD2CoreConstants.APPTAG+"-ELM327BTClient";
       private static final boolean D = true;

       private final BluetoothSocket mmSocket;
       private final InputStream mmInStream;
       private final OutputStream mmOutStream;

       private final Handler mHandler;

       private Scanner scannerInStr;

       private static final String AT_RESET[] = {"ATZ","Reset"};
       private static final String AT_OK[] = {"OK","Response OK"};
       private static final String AT_ECHO_ON[] = {"ATE1","ECHO ON"};
       private static final String AT_ECHO_OFF[] = {"ATE0","ECHO OFF"};
       private static final String AT_MEM_DISABLE[] = {"ATM0","MEMORY DISABLE"};
       private static final String AT_MEM_ENABLE[] = {"ATM1","MEMORY ENABLE"};
       private static final String AT_LF_CR_ONLY[] = {"ATL0","LINE FEED CARRIAGE RETURN ONLY"};
       private static final String AT_LF_CR_PLUS[] = {"ATL1","LINE FEED CARRIAGE RETURN +"};
       private static final String AT_NO_SPACE[] = {"ATS0","NO SPACE"};
       private static final String AT_ADD_SPACE[] = {"ATS1","ADD SPACE"};
       private static final String AT_DEV_DESCRIPTION[] = {"AT@1","DEVICE DESCRIPTION"};
       private static final String AT_IDENTIFY_ITSELF[] = {"ATI","IDENTIFY ITSELF"};
       private static final String AT_ADAPTIVE_TIMING_0[] = {"ATAT0","SET ADAPTIVE TIMING = 0"};
       private static final String AT_ADAPTIVE_TIMING_1[] = {"ATAT1","SET ADAPTIVE TIMING = 1"};
       private static final String AT_ADAPTIVE_TIMING_2[] = {"ATAT2","SET ADAPTIVE TIMING = 2"};
       private static final String AT_HEADERS_OFF[] = {"ATH0","TURN OFF HEADERS"};
       private static final String AT_HEADERS_ON[] = {"ATH1","TURN ON HEADERS"};
       private static final String AT_SET_PROTOCOL_AUTOMATIC[] = {"ATSP0","SET AUTOMATIC PROTOCOL SEARCH"};
       private static final String AT_DESCRIBE_PROTOCOL_NUMBER[] = {"ATDPN","DESCRIBE PROTOCOL NUMBER"};

       private static final byte[] TERM_SEND = {0x0d};
       private static final char TERM_RECEIVE = '>';

       private static final String OBD_SUPPORTED_PIDS_1_20[] = {"0100","WHAT ARE SUPPORTED PIDs from 0x01 to 0x20?"};
       private static final String OBD_SUPPORTED_PIDS_21_40[] = {"0120","WHAT ARE SUPPORTED PIDs from 0x21 to 0x40?"};
       private static final String OBD_SUPPORTED_PIDS_41_60[] = {"0140","WHAT ARE SUPPORTED PIDs from 0x41 to 0x60?"};
       private static final String OBD_SUPPORTED_PIDS_61_80[] = {"0160","WHAT ARE SUPPORTED PIDs from 0x61 to 0x80?"};
       private static final String OBD_SUPPORTED_PIDS_81_A0[] = {"0180","WHAT ARE SUPPORTED PIDs from 0x81 to 0xA0?"};


       //private OBD2CoreConfiguration obd2CoreConfigs;
       private AppFileLogger appfilelogger;
       private BluetoothCommandService mCommandService;

       public ELM327BluetoothClient(BluetoothCommandService mCommandService, BluetoothSocket socket, Handler mHandler) {
           Log.d(TAG, "create ELM327BTClient");
           this.mHandler=mHandler;

           OBD2CoreConfiguration obd2CoreConfigs = OBD2CoreConfiguration.getInstance();
           appfilelogger = obd2CoreConfigs.getAppFileLogger();
           mmSocket = socket;
           this.mCommandService = mCommandService;
           InputStream tmpIn = null;
           OutputStream tmpOut = null;

           // Get the BluetoothSocket input and output streams
           try {
               tmpIn = socket.getInputStream();
               tmpOut = socket.getOutputStream();


           } catch (IOException e) {
               Log.e(TAG, "temp sockets not created", e);
           }

           mmInStream = tmpIn;
           mmOutStream = tmpOut;
           scannerInStr=new Scanner(mmInStream);
       }

       public void run() {
           Log.i(TAG, "BEGIN ELM327BTClient");

           try {

               String responseAT;

               responseAT = new String(sendReceive(AT_LF_CR_ONLY[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               responseAT = new String(sendReceive(AT_RESET[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               responseAT = new String(sendReceive(AT_ECHO_OFF[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               responseAT = new String(sendReceive(AT_MEM_DISABLE[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               responseAT = new String(sendReceive(AT_LF_CR_ONLY[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               responseAT = new String(sendReceive(AT_NO_SPACE[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               responseAT = new String(sendReceive(AT_HEADERS_OFF[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               responseAT = new String(sendReceive(AT_ADAPTIVE_TIMING_1[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               responseAT = new String(sendReceive(AT_SET_PROTOCOL_AUTOMATIC[0]));
               Log.i(OBD2CoreConstants.BTMSGTAG,responseAT);

               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e2) {
                   // TODO Auto-generated catch block
                   e2.printStackTrace();
               }

               String pid0100response;

               while((pid0100response =  new String(sendReceive(OBD_SUPPORTED_PIDS_1_20[0]))).startsWith("SEARCHING")){
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e2) {
                       // TODO Auto-generated catch block
                       e2.printStackTrace();
                   }
               }

               String supportedpidHex1To20="00000000";
               String supportedpidHex21To40="00000000";
               String supportedpidHex41To60="00000000";
               String supportedpidHex61To80="00000000";
               String supportedpidHex81ToA0="00000000";

               try {
                   supportedpidHex1To20 = sendRequestSupportedPIDsAndValidateResp(OBD_SUPPORTED_PIDS_1_20[0]);
                   supportedpidHex21To40 = sendRequestSupportedPIDsAndValidateResp(OBD_SUPPORTED_PIDS_21_40[0]);
                   supportedpidHex41To60 = sendRequestSupportedPIDsAndValidateResp(OBD_SUPPORTED_PIDS_41_60[0]);
                   supportedpidHex61To80 = sendRequestSupportedPIDsAndValidateResp(OBD_SUPPORTED_PIDS_61_80[0]);
                   supportedpidHex81ToA0 = sendRequestSupportedPIDsAndValidateResp(OBD_SUPPORTED_PIDS_81_A0[0]);

               } catch (FormatException e2) {
                   // TODO Auto-generated catch block
                   e2.printStackTrace();
               }

               String supportedAllPIDsInHex = supportedpidHex1To20 + supportedpidHex21To40 + supportedpidHex41To60 +supportedpidHex61To80 +
                       supportedpidHex81ToA0 ;

               OBD2CoreConfiguration obd2CoreConfigs = OBD2CoreConfiguration.getInstance();
               JSONObject pidConfig = obd2CoreConfigs.getPidConfig();
               ArrayList<String> allPIDsList = obd2CoreConfigs.getAllPIDsList();
               boolean[] supportedPIDsInBinary = getSupportedPIDsInBinary(supportedAllPIDsInHex);
               ArrayList<String> supportedPIDList = selectSupportedPIDsFromAllPIDList(allPIDsList, supportedPIDsInBinary);
               obd2CoreConfigs.setSupportedPIDsBool(supportedPIDsInBinary);
               obd2CoreConfigs.setSupportedPIDsList(supportedPIDList);

               // TODO change according to car connected or not, todo properly handle notifications

               /*
               Message msg = mHandler
                       .obtainMessage(MainActivity.MESSAGE_CAR_CONNECTED_STATUS);
               Bundle bundle = new Bundle();
               bundle.putInt(MainActivity.CAR_CONNECTED, MainActivity.MESSAGE_OK);
               msg.setData(bundle);
               mHandler.sendMessage(msg);
               */

               OBD2EventManager obd2EventManager = OBD2EventManager.getInstance();

               while (!obd2CoreConfigs.isExitting()) {

                   ArrayList<String> queryPIDsList = obd2CoreConfigs.getQueryPIDsList();

                   //Initialize a new json data object to send.
                   JSONObject dataobj = new JSONObject();
                   JSONObject datapairs = new JSONObject();

                   for (int i = 0; i < queryPIDsList.size(); i++) {
                       byte[] temp;
                       String pid, tosend;
                       JSONObject pidobj;

                       pid = queryPIDsList.get(i);
                       try {
                           pidobj = pidConfig.getJSONObject(pid);

                           tosend = pidobj.getString(OBD2CoreConstants.CONF_MODE)
                                   + pidobj.getString(OBD2CoreConstants.CONF_PID);
                           int respbytes = pidobj.getInt(OBD2CoreConstants.CONF_BYTES);

                           Log.d(OBD2CoreConstants.BTMSGTAG, ">>" + tosend);
                           temp = sendReceive(tosend);

                           if (temp != null && temp.length != 0) {
                               String response = new String(temp);

                               Log.d(OBD2CoreConstants.APPTAG + "RESP", response);

                               String exprString = pidobj
                                       .getString(OBD2CoreConstants.CONF_EXPRESSION);

                               Expr expr = Parser.parse(exprString);

                               for (int j = 0; j < respbytes; j++) {
                                   String letter = Character
                                           .toString((char) (j + 65));
                                   String b = (response.charAt(response.length()
                                           - j * 2 - 1 - 1) + "")
                                           + response.charAt(response.length() - j
                                                   * 2 - 1);
                                   int ib = Integer.parseInt(b, 16);
                                   Variable.make(letter).setValue(ib);
                               }

                               double exprValue = expr.value();
                               Log.d("EXPR", pid + ":" + exprValue);

                               long curtime = System.currentTimeMillis();

                               datapairs.put(pid, OBD2CoreUtils.createTimeValuePair(
                                       curtime, exprValue));

                           }

                       } catch (JSONException e) {
                           // TODO Auto-generated catch block
                           e.printStackTrace();
                       } catch (SyntaxException e) {
                           // TODO Auto-generated catch block
                           e.printStackTrace();
                       }
                   } // for

                   try {
                       JSONObject timeobj = new JSONObject();
                       timeobj.put(OBD2CoreConstants.VALUE, System.currentTimeMillis());
                       datapairs.put(OBD2CoreConstants.SIDDHI_TIMESTAMP, timeobj);

                       dataobj.put(OBD2CoreConstants.OBDII_REAL_TIME_DATA, datapairs);

                       Log.i(OBD2CoreConstants.APPTAG, dataobj.toString());
                       obd2EventManager.addDataObj(dataobj);

                   } catch (JSONException e1) {
                       // TODO Auto-generated catch block
                       e1.printStackTrace();
                   }

                   try {
                       sleep (obd2CoreConfigs.getOBD2UpdateSpeed());
                   } catch (InterruptedException ignored) {
                   }

               } //while

               mmSocket.close();

           } catch (Exception e1) {

               // END

               //Change indicator icons is done by this too.
               mCommandService.stop();
               OBD2CoreConfiguration obd2CoreConfigs = OBD2CoreConfiguration.getInstance();
               obd2CoreConfigs.setSupportedPIDsBool(new boolean[OBD2CoreConstants.MAX_PIDS_SUPPORTED]);

               e1.printStackTrace();
               appfilelogger.println(Log.getStackTraceString(e1),true);

           }

       }

       public String sendRequestSupportedPIDsAndValidateResp(String PID) throws FormatException, IOException{

           String supportedpidsReponse = new String(sendReceive(PID));
           try{
               String supportedpidHex = supportedpidsReponse.substring(supportedpidsReponse.length() - OBD2CoreConstants.SUPPORTED_PIDS_RESPONSE_LENGTH);
               Log.i(OBD2CoreConstants.BTMSGTAG, supportedpidHex);
               new BigInteger(supportedpidHex, 16); //just check whether it can be converted to an integer..
               return supportedpidHex;
           }catch(Exception e){
               throw new FormatException();
           }
       }

       public static boolean[] getSupportedPIDsInBinary(String hexStr){

           int numcount = hexStr.length() * 4;

           String binary= new BigInteger(hexStr, 16).toString(2);

           if(binary.length()<numcount){
               binary = new String(new char[numcount - binary.length()]).replace("\0", "0") + binary;
           }

           boolean[] supportedpids = new boolean[numcount+1]; //+1 - pid 00 is always true
           supportedpids[0]=true;

           for(int i=0;i<binary.length();i++){
               if(binary.charAt(i)=='1'){
                   supportedpids[i+1]=true;
               }else{
                   supportedpids[i+1]=false;
               }
           }

           return supportedpids;
       }


       public static ArrayList<String> selectSupportedPIDsFromAllPIDList(ArrayList<String> allpidslist,boolean[] binaryarr){

           ArrayList<String> supportedPIDsList = new  ArrayList<String>();
           JSONObject pidconf = OBD2CoreConfiguration.getInstance().getPidConfig();
           try {
               for(int i=0;i<allpidslist.size();i++){
                   String pidKey =  allpidslist.get(i);
                   JSONObject pidobj =pidconf.getJSONObject(pidKey);
                   String pidHex = pidobj.getString(OBD2CoreConstants.CONF_PID);

                   int pidNum = Integer.parseInt(pidHex,16);

                   if(pidNum < binaryarr.length && binaryarr[pidNum]==true){
                       supportedPIDsList.add(pidKey);
                   }

               }
           } catch (JSONException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }

           return supportedPIDsList;
       }


       /**
        * Write to the connected OutStream.
        * @param buffer  The bytes to write
        */
       public void write(byte[] buffer) {
           try {
               mmOutStream.write(buffer);
           } catch (IOException e) {
               Log.e(TAG, "Exception during write", e);
           }
       }

       public void write(String str,byte[] term) {
           byte[] one = str.getBytes();
           byte[] two = term;
           byte[] combined = new byte[one.length + two.length];
           System.arraycopy(one,0,combined,0         ,one.length);
           System.arraycopy(two,0,combined,one.length,two.length);
           write(combined);
       }


       public void write(int out) {
           try {
               mmOutStream.write(out);
           } catch (IOException e) {
               Log.e(TAG, "Exception during write", e);
           }
       }

       public byte[] sendReceive(String command) throws IOException{

           if (appfilelogger != null) {
               appfilelogger.println(">> " + command);
           }

           byte[] buffer = new byte[1024];
           int char1=-1;

           int i=0;
           int length= mmInStream.available();

           for(int j=0;j<length;j++){
               char1 = mmInStream.read();
           }

           length= mmInStream.available();

           write(command,TERM_SEND);

           length= mmInStream.available();

           while((char1 = mmInStream.read())!= '>'){
               buffer[i]=(byte)char1;
               i++;
           }

           String tmp = new String(buffer, 0, i);

//        	String[] lines = tmp.split("(\n|\r)+");
           String response=tmp.trim();
//        	String response=lines[lines.length-2].trim();

           if (appfilelogger != null) {
               appfilelogger.println("<< " + response);
           }
           return response.getBytes();

       }

       public void cancel() {
           try {
               mmSocket.close();
           } catch (IOException e) {
               Log.e(TAG, "close() of connect socket failed", e);
           }
       }
   }