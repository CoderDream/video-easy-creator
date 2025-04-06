package com.coderdream.util.bbc;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import org.junit.jupiter.api.Test;

class ProcessScriptUtilTest {

  @Test
  void translateByGemini() {
    String folderName = "250403";
    String scriptDialogNew2FileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog_new2",
      CdConstants.TXT_EXTENSION);
    String scriptDialogNew2GeminiFileName = CdFileUtil.addPostfixToFileName(
      scriptDialogNew2FileName, "_gemini");

    ProcessScriptUtil.translateByGemini(scriptDialogNew2FileName,
      scriptDialogNew2GeminiFileName);
  }


  @Test
  void translateByGrok() {
    String folderName = "250403";
    String scriptDialogNew2FileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog_new2",
      CdConstants.TXT_EXTENSION);
    String scriptDialogNew2GrokFileName = CdFileUtil.addPostfixToFileName(
      scriptDialogNew2FileName, "_grok");
    ProcessScriptUtil.translateByGrok(scriptDialogNew2FileName,
      scriptDialogNew2GrokFileName);
  }


  @Test
  void translateScriptDialogByGemini() {
    String folderName = "250403";
    String scriptDialogNew2FileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog1",
      CdConstants.TXT_EXTENSION);
    String scriptDialogNew2GeminiFileName = CdFileUtil.addPostfixToFileName(
      scriptDialogNew2FileName, "_gemini");

    ProcessScriptUtil.translateByGemini(scriptDialogNew2FileName,
      scriptDialogNew2GeminiFileName);
  }


  @Test
  void translateScriptDialogByGrok() {
    String folderName = "250403";
    String scriptDialogNew2FileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog1",
      CdConstants.TXT_EXTENSION);
    String scriptDialogNew2GrokFileName = CdFileUtil.addPostfixToFileName(
      scriptDialogNew2FileName, "_grok");
    ProcessScriptUtil.translateByGrok("dialog", scriptDialogNew2FileName,
      scriptDialogNew2GrokFileName, 10);
  }
}
