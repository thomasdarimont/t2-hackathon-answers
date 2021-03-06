/*
 * Copyright (c) 2014, "SkyKeeper Team". All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package tr.com.t2.hackathon.answers.q1;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import tr.com.t2.hackathon.answers.Answers.BaseReducer;

/**
 * @author Serkan OZAL
 */
public class Question1Reducer extends BaseReducer<Text, IntWritable, Text, LongWritable> {

    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        try {
            int sum = 0;
            Iterator<IntWritable> i = values.iterator();
            // Calculate count of tweets with specific country code for key
            while (i.hasNext()) {
                sum += i.next().get();
            }
            // Emit country code and its occurrence value
            context.write(key, new LongWritable(sum));
        }
        catch (Throwable t) {
            logger.error("Error occured while executing reduce function of Reducer", t);
        }    
    }

}
