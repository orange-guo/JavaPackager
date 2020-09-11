package io.github.fvarrui.javapackager.maven;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFunction;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public class CreateZipball implements PackagerFunction {

	@Override
	public File apply(Packager packager) {
		
		File assetsFolder = packager.getAssetsFolder();
		String name = packager.getName();
		String version = packager.getVersion();
		Platform platform = packager.getPlatform();
		File outputDirectory = packager.getOutputDirectory(); 

		try {

			// generate assembly.xml file 
			File assemblyFile = new File(assetsFolder, "assembly-zipball-" + platform + ".xml");
			VelocityUtils.render(platform + "/assembly.xml.vtl", assemblyFile, packager);
			
			// zip file name and format
			String finalName = name + "-" + version + "-" + platform;
			String format = "zip";
			
			// invokes plugin to assemble zipball and/or tarball
			executeMojo(
					plugin(
							groupId("org.apache.maven.plugins"), 
							artifactId("maven-assembly-plugin"), 
							version("3.1.1")
					),
					goal("single"),
					configuration(
							element("formats", element("format", format)),
							element("descriptors", element("descriptor", assemblyFile.getAbsolutePath())),
							element("finalName", finalName),
							element("appendAssemblyId", "false")
					),
					Context.getMavenContext().getEnv()
				);

			return new File(outputDirectory, finalName + "." + format); 
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
			
		}

	}

}