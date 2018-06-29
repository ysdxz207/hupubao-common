/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package win.hupubao.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Moses
 * @date 2017-08-04
 */
public class ResourceUtils {
    private static Properties properties;

    public static InputStream readFile(String path) {
        InputStream inputStream = ResourceUtils.class
        .getClassLoader().getResourceAsStream(path);

        if (inputStream == null) {
            throw new RuntimeException("Can not find file " + path);
        }


        return inputStream;
    }

    public static Properties load(String path) {

        properties = new Properties();
        try {
            properties.load(readFile(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    public static String get(String key) {
        if (properties == null) {
            throw new RuntimeException("Properties not load yet.");
        }

        return (String) properties.get(key);
    }

    public static URL getResource(String filepath) {
        return ResourceUtils.class.getClassLoader().getResource(filepath);
    }

    public static String[] getResourceFolderFiles (String folder) {
        List<String> filenameList = new ArrayList<>();
        final File jarFile = new File(ResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if(jarFile.isFile()) {  // Run with JAR file
            try (JarFile jar = new JarFile(jarFile)){
                final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                while(entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.startsWith(folder + "/")) { //filter according to the path
                        filenameList.add(name.replace(folder + "/", ""));
                    }
                }

                Collections.sort(filenameList);
                return filenameList.toArray(new String[filenameList.size()]);
            } catch (Exception e) {

            }

        } else { // Run with IDE
            final URL url = getResource(folder);
            if (url != null) {
                try {
                    final File apps = new File(url.toURI());
                    return apps.list();
                } catch (URISyntaxException ex) {
                    // never happens
                }
            }
        }
        return new String[0];
    }
}
