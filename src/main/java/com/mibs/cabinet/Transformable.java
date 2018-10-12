package com.mibs.cabinet;

import com.mibs.cabinet.exception.ErrorDicomParsingException;
import com.mibs.cabinet.exception.ErrorTransferDICOMException;

import jcifs.smb.SmbFile;

public interface Transformable {

	int transferDICOMFiles(SmbFile[] files) throws ErrorTransferDICOMException ;
		
	int parsingDICOMFiles() throws ErrorDicomParsingException;	
	
	void clearImageTable();
	void saveImageEntity(Long id, Long instance, Long seria) throws Exception;
	
	void execute();
}
