package com.example.backend.mapred;

import com.example.backend.utils.WordCount;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.text.DecimalFormat;

public class AverageMR {
    static {
        try {
            System.load("D:\\hadoop-3.1.3\\bin\\hadoop.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }
    private final Configuration configuration;
    /**
     * 根据参数判断逆序正序
     */
    public AverageMR() {
        configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://47.115.231.140:9000");
        configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        configuration.set("dfs.client.use.datanode.hostname", "true");
        configuration.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        configuration.setBoolean("dfs.client.block.write.replace-datanode-on-failure.enabled",true);
    }

    public void run (Boolean desc, String[] files)
            throws IOException, InterruptedException, ClassNotFoundException {
        Job job = new Job(configuration, "WordCount");
        job.setJarByClass(AverageMR.class);
        job.setMapperClass(AverageMR.avgMapper.class);
        job.setReducerClass(AverageMR.avgReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(MyInputFormat.class);

        for (String file : files) {
            FileInputFormat.addInputPath(job, new Path("/inputs/" + file));
        }

        FileOutputFormat.setOutputPath(job, new Path("/output"));

        System.exit(job.waitForCompletion(true)?0:1);
    }

    public static class avgMapper extends Mapper<Object, Text, Text, Text> {
        private final Text subj = new Text();
        private final Text score = new Text();

        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\n");
            String[] headers = split[0].split("\\s+");

            for (int i = 1; i < split.length; i ++) {
                String line = split[i];
                String[] cells = line.split("\\s+");
                for (int j = 2; j < cells.length; j ++) {
                    subj.set(headers[j]);
                    score.set(cells[j]);
                    context.write(subj, score);
                }
            }
        }
    }

    public static class avgReducer extends Reducer<Text, Text, Text,Text> {
        private final Text text = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            int n = 0;
            double sum = 0;
            for (Text text : values) {
                sum += Double.parseDouble(text.toString());
                n ++;
            }
            if(n != 0) sum /= n;
            text.set(String.format("%.2f", sum));
            context.write(key, text);
        }
    }


}