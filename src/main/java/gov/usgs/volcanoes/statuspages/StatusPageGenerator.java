package gov.usgs.volcanoes.statuspages;

import gov.usgs.volcanoes.core.legacy.Arguments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main entrypoint for StatusPageGenerator program.
 * 
 * @author Nathan Ducasse, Bill Tollett
 *
 */
public class StatusPageGenerator {

  private List<SaveChartTask> tasks;
  ScheduledExecutorService threadPool;
  StatusGeneratorConfig config;

  /**
   * Constructor. Initializes thread pool, task list, and parses the config file
   *
   * @param confFile - Config file passed in via -c flag
   */
  public StatusPageGenerator(String confFile) {
    tasks = new ArrayList<SaveChartTask>();
    threadPool = new ScheduledThreadPoolExecutor(15);
    config = new StatusGeneratorConfig(confFile);
  }

  /**
   * Loops through parsed config object and creates task objects for each. Outputs php file mapping
   * filenames to Valve3 urls. Adds task to ScheduleExecutorService.
   *
   * @param directory - Location to save files.
   */
  public void startThreads(String directory) {
    String[] channels;
    SaveChartTask task;
    int initDelay = 0;

    // Create tasks
    for (Map.Entry<String, PageConfig> page : config.getPageConfigMap().entrySet()) {
      for (Map.Entry<String, Integer> period : page.getValue().getTimePeriods().entrySet()) {
        if (page.getValue().getChannels().isEmpty()) {
          channels = new String[4];
          task = new SaveChartTask(page.getValue(), period.getKey(), channels, directory);
          tasks.add(task);
        } else {
          for (List<String> channelList : page.getValue().getChannels()) {
            channels = new String[4];
            if (page.getValue().getMultiChannelFlag()) {
              int channelListIndex = 0;
              for (String channel : channelList) {
                channels[channelListIndex] = channel;
                channelListIndex++;
              }
              task = new SaveChartTask(page.getValue(), period.getKey(), channels, directory);
              tasks.add(task);
            } else {
              for (String channel : channelList) {
                channels[0] = channel;
                task = new SaveChartTask(page.getValue(), period.getKey(), channels, directory);
                tasks.add(task);
              }
            }
          } // ForEach List of channels
        } // If there are channels
      } // ForEach Time Period
    } // ForEach Config Page

    writePhp(directory);

    // Start tasks
    for (SaveChartTask thisTask : tasks) {
      threadPool.scheduleAtFixedRate(thisTask, initDelay, thisTask.getInterval(), TimeUnit.SECONDS);
    }
  }

  /**
   * Writes php file containing a map of image names to valve urls.
   *
   * @param directory location to write to
   */
  private void writePhp(String directory) {
    PrintWriter output = null;

    try {
      output = new PrintWriter(new FileWriter(directory + "urls.php"));
      output.println("<?php");
      output.println("$URLS = Array (");
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (SaveChartTask task : this.tasks) {
      output.println(
          "\'" + task.getFilename() + "\'\t=>\t\'/valve3/valve3.jsp?" + task.getUrl() + "\',");
    }

    output.println(");");
    output.println("?>");
    output.close();
  }

  /**
   * Main method for StatusPageGenerator. Takes a config file (-c) and save directory (-d) as
   * arguments and uses those values to create the StatusPageGenerator object which handles all the
   * multithreading
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("-c Config file");
      System.out.println("-d Directory to save images to");
      System.exit(1);
    }
    
    // Set up command-line args
    Set<String> keys = new HashSet<String>();
    keys.add("-c");
    keys.add("-d");

    Arguments cliargs = new Arguments(args, null, keys);
    String confFile = cliargs.get("-c");
    String directory = cliargs.get("-d");

    if (!directory.endsWith("/")) {
      directory += "/";
    }

    StatusPageGenerator gen = new StatusPageGenerator(confFile);
    gen.startThreads(directory);
  }
}
