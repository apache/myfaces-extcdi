Release Notes - MyFaces CODI - Version 1.0.6

** Bug

    * [EXTCDI-286] - release doesn't contain all artifacts
    * [EXTCDI-289] - windowhandler.html on the browser sometimes contains unused chunk at it's end.
    * [EXTCDI-302] - PartialViewContext doesn't get CodiFacesContextWrapper
    * [EXTCDI-304] - CODI @ConversationScope + @Stateful in a bean = java.lang.StackOverflowError
    * [EXTCDI-305] - review validation of EXTCDI-265
    * [EXTCDI-311] - merge back DELTASPIKE-471

** Improvement

    * [EXTCDI-284] - ClientSideWindowHandler currently only checks for an empty window.name
    * [EXTCDI-288] - [CSWH] WindowContextConfig#isUnknownWindowIdsAllowed() should default to "true"
    * [EXTCDI-291] - [CSWH] add UserAgent hook to ClientConfig
    * [EXTCDI-296] - Allow errorView navigation in all cases
    * [EXTCDI-297] - merge back DELTASPIKE-212
    * [EXTCDI-298] - [perf] reduce overhead in ClassUtils
    * [EXTCDI-308] - EAR support for Websphere 8
    * [EXTCDI-312] - support for weld 2.0.x
    * [EXTCDI-313] - ee7 support

** New Feature

    * [EXTCDI-287] - [CSWH] store fallback (noscriptUrl) decission in a cookie

** Task

    * [EXTCDI-314] - preparations for v1.0.6