package com.han.hdfs;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HDFSDemo {


    @Test
    public void listFile() throws URISyntaxException, IOException {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"),
                new Configuration());
        RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fileSystem.
                listFiles(new Path("/"), true);
        while (locatedFileStatusRemoteIterator.hasNext()) {
            LocatedFileStatus fileStatus = locatedFileStatusRemoteIterator.next();
            System.out.println(fileStatus.getPath() + "" + fileStatus.getPath().toString());
        }
    }

    @Test
    public void mkdir() throws URISyntaxException, IOException {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"),
                new Configuration());
        fileSystem.mkdirs(new Path("/dell"));
        fileSystem.close();
    }

    @Test
    public void download() throws IOException, URISyntaxException {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"),
                new Configuration());
        FSDataInputStream fsDataInputStream = fileSystem.open(new Path("/core-site.xml"));
        FileOutputStream outputStream = new FileOutputStream(new File("d:/hdfs.xml"));
        IOUtils.copy(fsDataInputStream, outputStream);
        IOUtils.closeQuietly(fsDataInputStream);
        IOUtils.closeQuietly(outputStream);
        fileSystem.close();
    }

    @Test
    public void upload() throws IOException, URISyntaxException {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"),
                new Configuration());
        fileSystem.copyFromLocalFile(new Path("file:///d:/tomcat.keystore"), new Path("/dell"));
        fileSystem.close();
    }

    @Test
    public void mergeFile() throws IOException, URISyntaxException, InterruptedException {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://node01:8020"),
                new Configuration(), "root");
        FSDataOutputStream outputStream = fileSystem.create(new Path("/big.txt"));
        LocalFileSystem local = FileSystem.getLocal(new Configuration());
        FileStatus[] listFiles = local.listStatus(new Path("d:/hdfs-test"));
        for (FileStatus fileStatus : listFiles) {
            FSDataInputStream inputStream = local.open(fileStatus.getPath());
            IOUtils.copy(inputStream, outputStream);
            IOUtils.closeQuietly(inputStream);
        }
        IOUtils.closeQuietly(outputStream);
        local.close();
        fileSystem.close();
    }
}
