Release Notes - MyFaces CODI - Version 1.0.1

** Bug

    * [EXTCDI-213] - ProjectStageProducer doesn't evaluate JNDI settings correctly.

** Improvement

    * [EXTCDI-198] - Support for Java 1.5 (needed for WebSphere 6.1)
    * [EXTCDI-206] - move first attempt for restoring the window-id 
    * [EXTCDI-208] - CodiFacesContextWrapper.getExceptionHandler() tries to inject many times during one request/response
    * [EXTCDI-210] - invalid page requests should be ignored
    * [EXTCDI-215] - support for the generic support module of extval

** New Feature

    * [EXTCDI-172] - sub-groups for conversation groups
    * [EXTCDI-204] - injectable resource bundle
    * [EXTCDI-207] - implicit sub-groups for conversation groups
    * [EXTCDI-209] - advanced service loader
    * [EXTCDI-211] - injectable resource bundle value
    * [EXTCDI-212] - improve logging in the ActivationExtension and ProjectStage producer

** Task

    * [EXTCDI-214] - cleanup #defaultReadObject
