import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.math.*;

public class Analysis {

  private static String formatSeconds( long s) {
    int seconds = (int) (s%60);
    int minutes = (int) ((s/60)%60);
    int hours = (int) ((s/60/60)%24);
    int days = (int) ((s/60/60/24)%365);
    int years = (int) (s/60/60/24/365);
    return ""
      + ( (years==0)   ? "" : years   + " yrs " )
      + ( (days==0)    ? "" : days    + " days " )
      + ( (hours==0)   ? "" : hours   + " hr " )
      + ( (minutes==0) ? "" : minutes + " min " )
      + seconds + " sec";
  }

  private static void printAnalysis(ArrayList<LocalDateTime> t) {
    Collections.sort(t);
    System.out.print("\nPhoto count:\t"+t.size());
    DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d, u h:mm a");
    System.out.print("\nFirst photo:\t"+t.get(0).format(format));
    System.out.print("\nLast photo:\t" + t.get(t.size()-1).format(format));
    System.out.print("\nDuration:\t"+
    ChronoUnit.DAYS.between(t.get(0),t.get(t.size()-1))+" days");
    System.out.print("\n");
    long sec;
    long sum=0;
    long max=0;
    long min = ChronoUnit.SECONDS.between(t.get(0),t.get(1));
    ArrayList<Long> durations = new ArrayList<Long>();
    for (int i=0; i<t.size()-1; i++) {
      sec = ChronoUnit.SECONDS.between(t.get(i),t.get(i+1));
      sum += sec;
      min = (min>sec) ? sec : min;
      max = (max<sec) ? sec : max;
//      System.out.print("\n"+(i+1)+":\t"+sec+" seconds");
      durations.add(sec);
    }
    long mean = sum/durations.size();
    long var=0;
    for (int i=0; i<durations.size(); i++) {
      var = (durations.get(i)-mean)*(durations.get(i)-mean);
    }
    double sd = Math.sqrt(var/durations.size());
    System.out.print("\n");
    System.out.print("\nmin:\t"+formatSeconds(min));
    System.out.print("\nmax:\t"+formatSeconds(max));
    System.out.print("\nmean:\t"+formatSeconds(mean));
//    System.out.print("\nvar:\t"+var/t.size());
    System.out.print("\nsd:\t\u03C3="
      + formatSeconds(new BigDecimal(sd).longValue()));
    System.out.print("\n");
    System.out.print("\n\tPhoto\tTimestamp\t\tdeviation\tduration to next photo");
    double duration = 0;
    for (int i=0; i<durations.size(); i++) {
      System.out.print(
        "\n\t" + (i+1) + "\t" 
        + t.get(i).format(format)
        + "\t(\u2213 " 
        + new BigDecimal(
            Math.abs(durations.get(i)-mean)/sd
          ).round(new MathContext(3)) + " \u03C3"
        + ")\t" + formatSeconds(durations.get(i)) 
      );
    }
  }

  public static void main(String[] args) throws Exception {
    BufferedReader buffer = null;
    String line = null;

    ArrayList<LocalDateTime> timestamps = new ArrayList<LocalDateTime>();
    String pattern = "yyyy:MM:dd HH:mm:ss";
    try {
      buffer = new BufferedReader(new FileReader("timestamps.dat"));
      while ((line=buffer.readLine()) != null) {
        timestamps.add(LocalDateTime.parse(line, DateTimeFormatter.ofPattern(pattern)));
      }
      printAnalysis(timestamps);
    } 
    catch(Exception e) { e.printStackTrace(); }
    finally { if (buffer != null) { buffer.close(); } }
  }
}
