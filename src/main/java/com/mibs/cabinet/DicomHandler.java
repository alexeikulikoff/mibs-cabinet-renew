package com.mibs.cabinet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import com.mibs.cabinet.exception.ErrorDicomParsingException;
import com.mibs.cabinet.exception.ErrorTransferDICOMException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import org.apache.commons.cli.*;

public class DicomHandler extends AbstractDicomHandler implements Transformable {
	private int counter = 0;

	public DicomHandler(String dicomName) {
		super(dicomName);
	}

	private int copyFiles(String path, SmbFile[] files) throws IOException, InterruptedException {
		for (SmbFile file : files) {
			if (file.isDirectory()) {
				copyFiles(path, file.listFiles());
			} else {
				SmbFile src = new SmbFile(file.getCanonicalPath());
				InputStream input = src.getInputStream();
				OutputStream output = new FileOutputStream(path + "/" + src.getName());
				IOUtils.copy(input, output);
				System.out.println("Copying : " + src.getCanonicalPath());
				output.close();
				input.close();
				counter++;
			}
		}
		return counter;
	}

	@Override
	public int transferDICOMFiles(SmbFile[] files) throws ErrorTransferDICOMException {
		int result = 0;
		ExecutorService service = null;
		try {
			service = Executors.newSingleThreadExecutor();
			Future<Integer> future = service.submit(() -> copyFiles(createLocalDir(dicomPath, dicomName), files));
			result = future.get(timeout, TimeUnit.MINUTES);
		} catch (Exception e) {
			throw new ErrorTransferDICOMException("Error while file transfering!");

		} finally {
			if (service != null)
				service.shutdown();
		}
		return result;
	}

	@Override
	public int parsingDICOMFiles() throws ErrorDicomParsingException {
		int result = 0;
		ExecutorService service = null;
		try {
			service = Executors.newSingleThreadExecutor();
			Future<Integer> future = service.submit(() -> {
				int counter = 0;
				clearImageTable();

				try {
					createLocalDir(serializePath, dicomName);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				File[] dicoms = new File(dicomPath + "/" + dicomName).listFiles();
				for (File dicom : dicoms) {
					DicomInputStream dis = null;
					try {
						dis = new DicomInputStream(dicom);
						Attributes attrs = dis.readDataset(-1, -1);
						Integer instance = attrs.getInt(Tag.InstanceNumber, 0);
						Integer seria = attrs.getInt(Tag.SeriesNumber, 0);
						String imgFileName = serializePath + "/" + dicomName + "/" + seria + "-" + instance
								+ "-img.jpg";
						File imageFile = new File(imgFileName);
						byte[] data = convert(dicom, imageFile);
						// System.out.println("Size: " + data.length);
						createSerializedDicom(attrs, data);
						try {
							saveImageEntity(new Long(1), new Long(2), new Long(3));
						} catch (Exception e) {
							e.printStackTrace();
							// logger.error("Error while saving mageEntity : " + e.getMessage() );
							throw new ErrorDicomParsingException(
									"Error reading temporary image file: " + e.getMessage());
						}
						counter++;
					} catch (IOException e) {
						e.printStackTrace();
						throw new ErrorDicomParsingException("Error reading temporary image file: " + e.getMessage());
					} finally {
						if (dis != null) {
							try {
								dis.close();
							} catch (IOException e) {
								e.printStackTrace();
								// logger.error("Error while closing DicomInputStream : " + e.getMessage() );
							}
						}
					}
				}

				return counter;
			});

			result = future.get();

		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorDicomParsingException("Error parsing DICOM inside Future: " + e.getMessage());
		} finally {
			if (service != null)
				service.shutdown();
		}
		return result;

	}

	public static void main(String[] args) {

		Options options = new Options();

		Option user = new Option("u", "user", true, "User");
		user.setRequired(true);
		options.addOption(user);

		Option password = new Option("p", "password", true, "Password");
		password.setRequired(true);
		options.addOption(password);

		Option host = new Option("h", "host", true, "DICOM Host");
		host.setRequired(true);
		options.addOption(host);

		Option directory = new Option("d", "directory", true, "DICOM directory");
		directory.setRequired(true);
		options.addOption(directory);

		CommandLineParser parser = new BasicParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			
			formatter.printHelp("mibs-cabinet-renew.jar", options);

			System.exit(1);
		}

		String iUser = cmd.getOptionValue("user");
		String iPassword = cmd.getOptionValue("password");
		String iHost = cmd.getOptionValue("host");
		String iDirectory = cmd.getOptionValue("directory");

		if (iDirectory.startsWith("/"))
			iDirectory = iDirectory.substring(1);

		String path = "smb://" + iUser + ":" + iPassword + "@" + iHost + "/" + iDirectory;
		if (!path.endsWith("/"))
			path = path + "/";

		System.out.println(path);

		SmbFile[] files = null;
		try {
			files = new SmbFile(path).listFiles();

		} catch (SmbException | MalformedURLException e) {

			System.out.println("ERROR: Connection to " + "\"" + path + "\" was mercilessly refused!");
			System.exit(1);
		}

		String dicomName = "TEST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		DicomHandler handler = new DicomHandler(dicomName);
		int transfered_n = 0;
		int parsed_n = 0;
		try {
			transfered_n = handler.transferDICOMFiles(files);
			try {
				parsed_n = handler.parsingDICOMFiles();

			} catch (ErrorDicomParsingException e) {
				e.printStackTrace();
				System.out.println("Parsing dicom's files has faced an error!");
			}

		} catch (ErrorTransferDICOMException ex) {
			ex.printStackTrace();
			System.out.println("Transfer dicom's files has faced an error!");
		}

		System.out.println("Transfered: " + transfered_n + " Parsed: " + parsed_n);
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearImageTable() {
	}

	@Override
	public void saveImageEntity(Long id, Long instance, Long seria) throws Exception {

	}

	public String getGreeting() {
		return "test message";
	}
}
