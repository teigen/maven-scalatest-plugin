content = new File("src/it/default/build.log").text

return content.contains("OUT: Run starting. Expected test count is: 2") &&
content.contains("OUT: Suite Starting - DiscoverySuite: The execute method of a nested suite is about to be invoked.") &&
content.contains("OUT: Suite Starting - DefaultSuite: The execute method of a nested suite is about to be invoked.") &&
content.contains("OUT: Test Starting - DefaultSuite: first") &&
content.contains("OUT: Test Succeeded - DefaultSuite: first") &&
content.contains("OUT: Test Starting - DefaultSuite: second") &&
content.contains("OUT: Test Succeeded - DefaultSuite: second") &&
content.contains("OUT: Suite Completed - DefaultSuite: The execute method of a nested suite returned normally.") &&
content.contains("OUT: Suite Completed - DiscoverySuite: The execute method of a nested suite returned normally.") &&
content.contains("OUT: Run completed. Total number of tests run was: 2") &&
content.contains("OUT: All tests passed.") &&
content.contains("OUT: [INFO] BUILD SUCCESSFUL")        