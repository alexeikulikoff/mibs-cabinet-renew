package com.mibs.cabinet;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.image.BufferedImageUtils;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mibs.cabinet.exception.ErrorDicomParsingException;

abstract class AbstractDicomHandler {
	// static Logger logger = LoggerFactory.getLogger(AbstractDicomHandler.class);
	 
	 protected String serializePath;
	 protected String dicomPath;
	 protected int timeout;
	 
	 protected String dicomName;
	 
	 private int frame = 1;
	 private String suffix;
	 private ImageWriter imageWriter;
	 private ImageWriteParam imageWriteParam;
	 private static ResourceBundle rb = ResourceBundle.getBundle("messages");
	 public static ResourceBundle cfg = ResourceBundle.getBundle("application");
	 private final ImageReader imageReader;

	 public void initImageWriter(String formatName, String suffix, String clazz, String compressionType, Number quality) {
	        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(formatName);
	        if (!imageWriters.hasNext()) throw new IllegalArgumentException( MessageFormat.format(rb.getString("formatNotSupported"), formatName));
	        this.suffix = suffix != null ? suffix : formatName.toLowerCase();
	        imageWriter = imageWriters.next();
	        if (clazz != null)
	            while (!clazz.equals(imageWriter.getClass().getName()))
	                if (imageWriters.hasNext())
	                    imageWriter = imageWriters.next();
	                else
	                    throw new IllegalArgumentException( MessageFormat.format(rb.getString("noSuchImageWriter"), clazz, formatName));
	        imageWriteParam = imageWriter.getDefaultWriteParam();
	        if (compressionType != null || quality != null) {
	            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	            if (compressionType != null)
	                imageWriteParam.setCompressionType(compressionType);
	            if (quality != null)
	                imageWriteParam.setCompressionQuality(quality.floatValue());
	        }
	}
	 private BufferedImage readImage(ImageInputStream iis) throws IOException {
	        imageReader.setInput(iis);
	        return imageReader.read(frame-1, readParam());
	}
	 private ImageReadParam readParam() {
	        DicomImageReadParam param = (DicomImageReadParam) imageReader.getDefaultReadParam();
	        //param.setWindowWidth(512);
	        param.setAutoWindowing(true);
	        
	        return param;
	 }
	 private BufferedImage convert(BufferedImage bi) {
	        ColorModel cm = bi.getColorModel();
	        return cm.getNumComponents() == 3 ? BufferedImageUtils.convertToIntRGB(bi) : bi;
	    }

	 private void writeImage(ImageOutputStream ios, BufferedImage bi) throws IOException {
	        imageWriter.setOutput(ios);
	        imageWriter.write(null, new IIOImage(bi, null, null), imageWriteParam);
	        
	    }


	private String suffix(File src) {
	        return src.getName() + '.' + suffix;
	}

	 public byte[] convert(File src, File dest) throws IOException {
		 	byte[] result  = null;
	        ImageInputStream iis = ImageIO.createImageInputStream(src);
	        try {
	            BufferedImage bi = readImage(iis);
	            //ImageOutputStream ios = ImageIO.createImageOutputStream(dest);
	            try {
	               // writeImage(ios, bi);
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                ImageIO.write( bi, "jpg", baos );
	                baos.flush();
	                result = baos.toByteArray();
	                baos.close();
	                System.out.println("Convert: " +  src );
	            } finally {
	              //  try { ios.close(); } catch (IOException ignore) {}
	            }
	        } finally {
	            try { iis.close(); } catch (IOException ignore) {}
	        }
	        return result;
	    }
	 

	protected void createSerializedDicom(Attributes attrs, byte[] data) throws ErrorDicomParsingException {

		Integer instance= attrs.getInt(Tag.InstanceNumber, 0);
		Integer seria= attrs.getInt(Tag.SeriesNumber, 0);
		SerializedDicom sdic = new SerializedDicom();
		sdic.setImage(data );
		sdic.setSeria( attrs.getInt(Tag.SeriesNumber, 0) );
		sdic.setInstance( attrs.getInt(Tag.InstanceNumber, 0) );
		sdic.setPatientName(attrs.getString(Tag.PatientName));
		sdic.setPatientBirthDate(attrs.getString(Tag.PatientBirthDate));
		sdic.setPatientID(attrs.getString(Tag.PatientID));
		sdic.setPatientAge(attrs.getString(Tag.PatientAge));
		sdic.setPatientWeight(attrs.getString(Tag.PatientWeight));
		sdic.setPatientSex(attrs.getString(Tag.PatientSex));
		sdic.setInstitutionName(attrs.getString(Tag.InstitutionName));
		sdic.setStudyDate(attrs.getString(Tag.StudyDate));
		sdic.setStudyComment(attrs.getString(Tag.StudyComments));
		sdic.setStudyDescription(attrs.getString(Tag.StudyDescription));
		sdic.setSliceThickness(attrs.getString(Tag.SliceThickness));
		sdic.setPerformingPhysicianName(attrs.getString(Tag.PerformingPhysicianName));
		sdic.setSliceLocation(attrs.getString(Tag.SliceLocation));
		sdic.setRows(attrs.getInt(Tag.Rows, 0));
		sdic.setColumns(attrs.getInt(Tag.Columns, 0));
		sdic.setPixelSpacing(attrs.getString(Tag.PixelSpacing));
		sdic.setManufactoreModelName(attrs.getString(Tag.ManufacturerModelName));
		String serializedFile = serializePath + "/" + dicomName + "/data-" + seria + "-" + instance + ".ser";
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {
			fileOut = new FileOutputStream(serializedFile);
			out = new ObjectOutputStream(fileOut);
			out.writeObject( sdic );
			System.out.println("Create " + serializedFile + " with image size: " + data.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ErrorDicomParsingException("Error while creating SER directory: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ErrorDicomParsingException("Error writing to serialized object: " + e.getMessage());
		}finally {
			try {
				out.close();
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			//	logger.error("Error closing stream : " + e.getMessage() );
			}
		}
		
	}
	protected static String createLocalDir(String path, String name) throws  FileNotFoundException {
		String result = path + "/" + name;
		File destDir = new File( result );
		if (!destDir.exists()) {
			if (!destDir.mkdir()) throw new FileNotFoundException("Error: directory not created!");
		}
		return result;
	}
	public AbstractDicomHandler( String dicomName) {
		this.timeout = Integer.parseInt( cfg.getString("timeout") );
		this.serializePath = cfg.getString("serializePath");
		this.dicomPath = cfg.getString("dicomPath");
		this.dicomName = dicomName;
		 
		ImageIO.scanForPlugins();            
		imageReader = ImageIO.getImageReadersByFormatName("DICOM").next();
		initImageWriter("JPEG","jpeg", null, null,null);
	}

}
