/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class CopyAndRenameStudyImages extends StarMacro {

    String _studyDir = "/u/xeons24/people/aarong/optimate/projects/mbda/hxchanger/test/3dCadTest_opt2";

    @Override
    public void execute() {
        File postDir = new File(_studyDir + File.separator + "POST_0");

        if (!postDir.exists()) {
            getActiveSimulation().println("POST_0 directory does not exist in study dir: " + _studyDir + ". Stopping.");
            return;
        }

        File imgDir = new File(_studyDir + File.separator + "imgs");
        imgDir.mkdir();

        for (File designDir : postDir.listFiles()) {
            try {
                String design = designDir.getName().replace("Design", "").trim();
                if (!isError(design)) {
                    File star0dir = new File(designDir.getAbsolutePath() + File.separator + "star");
                    for (File f : star0dir.listFiles()) {
                        if (f.getName().endsWith(".png")) {
                            copy(star0dir.getAbsolutePath(), imgDir.getAbsolutePath(), f.getName(), f.getName().replace(".png", String.format("-%05d", Integer.parseInt(design)) + ".png"));
                        }
                    }
                }
            } catch (Exception ex) {
                print(ex);
            }
        }
    }

    boolean isError(String design) throws Exception {
        File resFile = new File(_studyDir + File.separator + "star0.res");
        boolean found = false;
        boolean error = false;
        int evalCol = -1;
        int flagCol = -1;

        try (Scanner scan = new Scanner(resFile)) {
            String header[] = scan.nextLine().split(",");

            for (int i = 0; i < header.length; i++) {
                if (header[i].toLowerCase().contains("evaluation")) {
                    evalCol = i;
                } else if (header[i].toLowerCase().contains("design flag")) {
                    flagCol = i;
                }
            }

            if (evalCol == -1 || flagCol == -1) {
                throw new Exception("star0.res file did not contain eval and/or flag column in header please verify the correct file is in the study folder.");
            }

            while (scan.hasNext()) {
                String data[] = scan.nextLine().split(",");
                if (data[evalCol].trim().toLowerCase().equals(design.trim())) {
                    found = true;
                    error = data[flagCol].trim().toLowerCase().contains("error");
                }
            }
        }

        if (!found) {
            throw new Exception("star0.res file did not contain an entry for Design " + design + " please verify the correct file is in the study folder.");
        } else {
            return error;
        }
    }

    void copy(String srcDir, String destDir, String srcName, String destName) throws IOException {
        Path source = new File(srcDir + File.separator + srcName).toPath();
        Path target = new File(destDir + File.separator + destName).toPath();
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    void print(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        getActiveSimulation().println(sw.toString());
    }

}
