package com.liferay.project.templates.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.maven.executor.MavenExecutor;
import com.liferay.maven.executor.internal.FileUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

@RunWith(Arquillian.class)
public class ArchetypeTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule = new LiferayIntegrationTestRule();
	
	@ClassRule
	public static final MavenExecutor mavenExecutor = new MavenExecutor();

	@BeforeClass
	public void setUp() throws Exception {
		if (_javaTempDir.exists()) {
			FileUtil.deleteDirectory(_javaTempDir.toPath());
		}
		
		List<String> completeArgs = new ArrayList<>();

		completeArgs.add("archetype:generate");
		completeArgs.add("--batch-mode");

		String archetypeArtifactId =
			"com.liferay.project.templates." + "mvc.portlet";

		completeArgs.add("-DarchetypeArtifactId=" + archetypeArtifactId);

		String projectTemplateVersion = "1.0.5";

		completeArgs.add("-DarchetypeGroupId=com.liferay");
		completeArgs.add("-DarchetypeVersion=" + projectTemplateVersion);
		completeArgs.add("-Dauthor=" + System.getProperty("user.name"));
		completeArgs.add("-DgroupId=" + "MVCPortlet");
		completeArgs.add("-DartifactId=" + "archetypetest-mvcportlet");
		completeArgs.add("-Dversion=1.0.0");
		completeArgs.add("-DprojectType=standalone");

		MavenExecutor.Result result = mavenExecutor.execute(new File(_javaTempDir, "archetypetest-mvcportlet"), completeArgs.toArray(new String[0]));

		Assert.assertEquals(result.output, 0, result.exitCode);
	}

	@Test
	public void testProjectInstall() throws Exception {
		// test project install
		Bundle bundle = FrameworkUtil.getBundle(ArchetypeTest.class);
		
		BundleContext bundleContext = bundle.getBundleContext();
		
		File testBundleFile = new File(_javaTempDir.getAbsolutePath() + "archetypetest-mvcportlet/target/MVCPortlet-1.0.0.jar");
		
		Assert.assertTrue(testBundleFile.exists());
		
		Bundle testBundle = bundleContext.installBundle(testBundleFile.getAbsolutePath());
		
		testBundle.start();

		Assert.assertEquals(Bundle.ACTIVE, testBundle.getState());
	}
	
	private static File _javaTempDir = new File(System.getProperty("java.io.tmpdir"));
}