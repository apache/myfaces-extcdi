Release Notes - MyFaces CODI - Version 0.9.3

** Bug

    * [EXTCDI-98] - ClientSideWindowHandler doesn't honour isUnknownWindowIdsAllowed
    * [EXTCDI-127] - Injection in FacesConverter does not work
    * [EXTCDI-131] - Lost ViewAccessScope when re post action with navigation
    * [EXTCDI-132] - CoDI crashes on Resin 4
    * [EXTCDI-143] - environment variable javax.faces.ProjectStage doesn't get recognized anymore

** Improvement

    * [EXTCDI-121] - CodiUtils#lookupFromEnvironment should also use the ServiceLoader
    * [EXTCDI-124] - state of ViewAccessConversationExpirationEvaluatorRegistry
    * [EXTCDI-125] - pagebeans as package based inline view-configs
    * [EXTCDI-134] - unified base-config
    * [EXTCDI-135] - support for hybrid view-config/navigation constellations
    * [EXTCDI-136] - revision number in manifest files
    * [EXTCDI-137] - detect the window-id as soon as possible
    * [EXTCDI-140] - explicitely annotate @Dependent scoped beans and make them non-final
    * [EXTCDI-141] - use maven-failsafe-plugin for running integration tests
    * [EXTCDI-145] - Use RenderKitWrapper (JSF 2.0) for InterceptedRenderKit
    * [EXTCDI-146] - re-visit lazy initialization

** New Feature

    * [EXTCDI-120] - StartupEventBroadcaster
    * [EXTCDI-138] - pluggable interceptor implementations

** Task

    * [EXTCDI-122] - revisit jndi names
    * [EXTCDI-139] - rename internal examples
