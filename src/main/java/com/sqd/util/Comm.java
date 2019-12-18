package com.sqd.util;

import com.sqd.file.FileInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Comm {
    public static Map<String, FileInfo> FILE_MAP = new ConcurrentHashMap<>();
}
