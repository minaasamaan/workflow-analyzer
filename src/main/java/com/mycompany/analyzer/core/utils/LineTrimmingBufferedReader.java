package com.mycompany.analyzer.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class LineTrimmingBufferedReader extends BufferedReader
{
    public LineTrimmingBufferedReader(Reader in)
    {
        super(in);
    }

    @Override
    public String readLine() throws IOException
    {
        String result = super.readLine();
        return result != null ? result.trim() : null;
    }
}
