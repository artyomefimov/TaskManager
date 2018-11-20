package com.a_team.taskmanager.backup;

import android.app.Application;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.a_team.taskmanager.backup.BackupConstants.EXCEPTION_PHOTO_WRITE;
import static com.a_team.taskmanager.backup.BackupConstants.FILE_NOT_FOUND_WRITE;
import static com.a_team.taskmanager.backup.BackupConstants.IO_EXCEPTION_WRITE;
import static com.a_team.taskmanager.backup.BackupConstants.READ_TASK_EXCEPTION;
import static com.a_team.taskmanager.backup.BackupUtil.getFileName;
import static com.a_team.taskmanager.backup.BackupUtil.getStringFromId;

public class FileDataWriter {
    private static final String TAG = "FileDataWriter";
    private List<File> files;

    public FileDataWriter() {
        files = new ArrayList<>();
    }

    public File writeTasksToBackup(Application app, List<Task> tasks) throws IOException {
        File file = new File("tasks.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            writeTasksToFiles(app, tasks);
            zipFiles(zos);
            deleteTempFiles();
        } catch (FileNotFoundException e) {
            throw new IOException(FILE_NOT_FOUND_WRITE, e);
        } catch (IOException e) {
            throw new IOException(IO_EXCEPTION_WRITE, e);
        }
        return file;
    }

    private void writeTasksToFiles(Application app, List<Task> tasks) throws IOException {
        for (Task task : tasks) {
            if (task != null) {
                File fileWithTask = writeTaskToFile(task);
                files.add(fileWithTask);

//                File photoFile = addPhotoToZip(app, task);
//                if (photoFile != null)
//                    files.add(photoFile);
            }
        }
    }

    private File writeTaskToFile(Task task) throws IOException {
        String filename = getFileName(getStringFromId(task.getId()));
        File fileWithTask = new File(filename);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileWithTask));

        oos.writeObject(task);
        oos.close();

        return fileWithTask;
    }

    private File addPhotoToZip(Application app, Task task) throws IOException {
        BasicApp myApp = ((BasicApp) app);
        TaskManagerRepository repository = myApp.getRepository();

        File photoFile = repository.getPhotoFile(task.getPhotoFilename());
        if (photoFile != null && photoFile.exists()) {
            return photoFile;
        } else {
            return null;
        }
    }

    private void zipFiles(ZipOutputStream zos) throws IOException {
        byte[] buffer = new byte[1024];

        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }

    private void deleteTempFiles() {
        for (File file : files) {
            file.delete();
        }
    }
}
