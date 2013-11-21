package util;

import android.util.Log;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

    // пишет файл со списком изображений, вызывается из галереи

	public void saveArray(String filename, List<String> output_field) {
	     try {
	        FileOutputStream fos = new FileOutputStream(filename);
	        GZIPOutputStream gzos = new GZIPOutputStream(fos);
	        ObjectOutputStream out = new ObjectOutputStream(gzos);
	        out.writeObject(output_field);
	        out.flush();
	        out.close();
	     }
	     catch (IOException e) {
	         e.getStackTrace(); 
	     }
	  }


    // norm, вызывается из галереи если файл со списком изображений уже существует

	public List<String> loadArray(String filename) {
	      try {

            // read file by path
	        FileInputStream fis = new FileInputStream(filename);

            //Этот класс реализует потоковый фильтр для того, чтобы считать сжатые данные в формате файла GZIP
	        GZIPInputStream gzis = new GZIPInputStream(fis);

            // десериализовывает примитивные данные и возражает ранее записанному использованию
	        ObjectInputStream in = new ObjectInputStream(gzis);
            // возвращает лист объектов изображений
	        List<String> read_field = (List<String>)in.readObject();
	        in.close();
	        return read_field;
	      }
	      catch (Exception e) {
	    	  e.getStackTrace();
	      }
	      return null;
	  }


	// метод выдающий лист изображений
	public File[] listFilesAsArray(File directory, FilenameFilter[] filter,
			int recurse) {
		Collection<File> files = listFiles(directory, filter, recurse);

		File[] arr = new File[files.size()];
		return files.toArray(arr);
	}

	public Collection<File> listFiles(File directory,
			FilenameFilter[] filter, int recurse) {

		Vector<File> files = new Vector<File>();

		File[] entries = directory.listFiles();

		if (entries != null) {
			for (File entry : entries) {
				for (FilenameFilter filefilter : filter) {
					if (filter == null
							|| filefilter
									.accept(directory, entry.getName())) {
						files.add(entry);
						Log.v("ImageViewFlipper", "Added: "
								+ entry.getName());
					}
				}
				if ((recurse <= -1) || (recurse > 0 && entry.isDirectory())) {
					recurse--;
					files.addAll(listFiles(entry, filter, recurse));
					recurse++;
				}
			}
		}
		return files;
	}
}