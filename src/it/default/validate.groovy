output = new File("src/it/default/build.log").text

stdoutOk = output.contains("OUT: Run starting. Expected test count is: 2") &&
output.contains("OUT: Suite Starting - DiscoverySuite: The execute method of a nested suite is about to be invoked.") &&
output.contains("OUT: Suite Starting - DefaultSuite: The execute method of a nested suite is about to be invoked.") &&
output.contains("OUT: Test Starting - DefaultSuite: first") &&
output.contains("OUT: Test Succeeded - DefaultSuite: first") &&
output.contains("OUT: Test Starting - DefaultSuite: second") &&
output.contains("OUT: Test Succeeded - DefaultSuite: second") &&
output.contains("OUT: Suite Completed - DefaultSuite: The execute method of a nested suite returned normally.") &&
output.contains("OUT: Suite Completed - DiscoverySuite: The execute method of a nested suite returned normally.") &&
output.contains("OUT: Run completed. Total number of tests run was: 2") &&
output.contains("OUT: All tests passed.") &&
output.contains("OUT: [INFO] BUILD SUCCESSFUL")

defaultSuiteContent = new File("src/it/default/target/surefire-reports/DefaultSuite.txt").text
defaultSuiteOk = defaultSuiteContent.contains("Test set: DefaultSuite") &&
defaultSuiteContent.contains("Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed:") &&
defaultSuiteContent.contains("DefaultSuite: first") &&
defaultSuiteContent.contains("DefaultSuite: second")

defaultSuiteXmlFileExists = new File("src/it/default/target/surefire-reports/TEST-DefaultSuite.xml").exists()

return stdoutOk && defaultSuiteOk && defaultSuiteXmlFileExists