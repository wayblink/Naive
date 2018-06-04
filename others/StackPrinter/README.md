  
  Scala版
  
  private def printTrace: String = {
    val sbf = new StringBuffer
    val st = Thread.currentThread.getStackTrace
    if (st == null) {
      sbf.append("...")
      return sbf.toString
    }
    for (e <- st) {
      if (sbf.length > 0) {
        sbf.append(" <- ")
        sbf.append(System.getProperty("line.separator"))
      }
      sbf.append(s"${e.getClassName}.${e.getMethodName}--${e.getLineNumber}")
    }
    sbf.toString
  }
  
  Java版

  String printTrack(){
        StringBuffer sbf =new StringBuffer();
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        if(st==null){
            sbf.append("无堆栈...");
            return sbf.toString();
        }
        for(StackTraceElement e:st){
            if(sbf.length()>0){
                sbf.append(" <- ");
                sbf.append(System.getProperty("line.separator"));
            }
            sbf.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    ,e.getClassName()
                    ,e.getMethodName()
                    ,e.getLineNumber()));
        }
        return sbf.toString();
    }
