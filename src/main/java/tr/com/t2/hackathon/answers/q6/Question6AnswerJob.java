/*
 * Copyright (c) 2014, "SkyKeeper Team". All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package tr.com.t2.hackathon.answers.q6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import tr.com.t2.hackathon.answers.Answers.BaseAnswerJob;
import tr.com.t2.hackathon.answers.io.LineInputFormat;

/**
 * @author Serkan OZAL
 */
public class Question6AnswerJob extends BaseAnswerJob {

    @Override
    public void doJob(String[] args, Job job, JobConf conf, Path inputPath, Path outputPath) {
        job.setJobName("Question 6");
        
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(MentionedTweetData.class);
        
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(LongWritable.class);
        
        job.setMapperClass(Question6Mapper.class);
        job.setReducerClass(Question6Reducer.class);

        job.setInputFormatClass(LineInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
    }
    
    @Override
    public void processResult(String[] args, Job job, JobConf conf, Path inputPath, Path outputPath, String outputFileName) {
        try {
            FileSystem outputFS = outputPath.getFileSystem(conf);
            Path outputFilePath = new Path(outputPath + "/" + outputFileName);
            
            InputStream is = outputFS.open(outputFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            Long maxScoreUserId = null;
            Long maxScore = null;
            // Find global maximum score with its owner user
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("[\\s]+");
                Long userId = Long.parseLong(parts[0]);
                Long score = Long.parseLong(parts[1]);
                if (maxScore == null) {
                    maxScore = score;
                    maxScoreUserId = userId;
                }
                else if(score > maxScore) {
                    maxScore = score;
                    maxScoreUserId = userId;
                }
            }
            is.close();
            
            // Delete old result file
            outputFS.delete(outputFilePath, true);
            
            OutputStream os = outputFS.create(outputFilePath);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            
            // Write result 
            bw.write(maxScoreUserId + " " + maxScore);
            
            bw.flush();
            bw.close();
        }
        catch (Throwable t) {
            logger.error("Error occured while processing result", t);
        }
    }

}
