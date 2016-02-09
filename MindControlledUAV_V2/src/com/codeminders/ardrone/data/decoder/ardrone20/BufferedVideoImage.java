package com.codeminders.ardrone.data.decoder.ardrone20;


// Copyright (C) 2007-2011, PARROT SA, all rights reserved.

// DISCLAIMER
// The APIs is provided by PARROT and contributors "AS IS" and any express or
// implied warranties, including, but not limited to, the implied warranties of
// merchantability
// and fitness for a particular purpose are disclaimed. In no event shall PARROT
// and contributors be liable for any direct, indirect, incidental, special,
// exemplary, or
// consequential damages (including, but not limited to, procurement of
// substitute goods or services; loss of use, data, or profits; or business
// interruption) however
// caused and on any theory of liability, whether in contract, strict liability,
// or tort (including negligence or otherwise) arising in any way out of the use
// of this software, even if advised of the possibility of such damage.

// Author : Daniel Schmidt
// Publishing date : 2011-07-15
// based on work by : Wilke Jansoone

// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// - Redistributions of source code must retain the above copyright notice, this
// list of conditions, the disclaimer and the original author of the source
// code.
// - Neither the name of the PixVillage Team, nor the names of its contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission.

public class BufferedVideoImage
{
    private int imageStreamCapacity;
    private int pixelRowSize;
    private int pictureType;
    private int frameIndex;

    private int sliceCount;

    private boolean pictureComplete;

    private int height;
    private int width;

    private int[] javaPixelData;
    private byte[] imageStreamByteArray;

    /*
     * Convert a stream to an image
     * 
     * Takes in bytes representing an image and renders the image after decoding the bytes.
     * 
     * @param ByteBuffer stream
     *      A ByteBuffer full of the bytes that represent the image to be decoded.
     */
    public void addImageStream(byte[] imageStreamByteArray, int actualDatalength)
    {
        this.imageStreamByteArray = imageStreamByteArray;
        imageStreamCapacity = actualDatalength;
        processStream();
    }    

    public int getFrameIndex() 
    {
        return frameIndex;
    }

    public int getHeight()
    {
        return height;
    }

    public int[] getJavaPixelData()
    {
        return javaPixelData;
    }

    public int getPictureType()
    {
        return pictureType;
    }

    public int getPixelRowSize()
    {
        return pixelRowSize;
    }

    public int getSliceCount()
    {
        return sliceCount;
    }

    public int getWidth()
    {
        return width;
    }

    private void processStream()
    {
        pictureComplete = false;

        while (!pictureComplete)
        {
            readHeader();

            if (!pictureComplete)
            {                
            }
        }
    }

    private void readHeader()
    {
        
    }

}
