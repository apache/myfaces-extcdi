Release Notes - MyFaces CODI - Version 1.0.0

** Bug

    * [EXTCDI-184] - mojarra causes a ContextNotActiveException
    * [EXTCDI-205] - GroupedConversationContext#isActive crashes with a NPE if FacesContext is not active or null

** Improvement

    * [EXTCDI-182] - invocation order for internal phase-listeners
    * [EXTCDI-196] - advanced version of ValidatorFactory#usingContext
    * [EXTCDI-197] - artifacts of the scripting module should be serializable
    * [EXTCDI-200] - TransactionalInterceptor: flush EntityManagers before commit
    * [EXTCDI-202] - simple OSGi support

** New Feature

  * [EXTCDI-174] - Introduce @PropertyActivated or @ConfigActivated
  * [EXTCDI-180] - dependency injection support for ExceptionHandler
  * [EXTCDI-181] - ViewExpiredException should be displayed by the DefaultErrorView
  * [EXTCDI-185] - optional property file based configuration
  * [EXTCDI-188] - BeanResolver
  * [EXTCDI-193] - messageContext support in el-expressions

** Task

  * [EXTCDI-44] - test the compatibility of codi-conversations
  * [EXTCDI-147] - create unit tests for all extension points to test custom implementations
  * [EXTCDI-177] - workaround for the @Alternative issue of weld
  * [EXTCDI-187] - move @ProjectStageActivated, Deactivatable and ClassDeactivator to the activation package
  * [EXTCDI-191] - workaround for the weld bean-caching issue
  * [EXTCDI-192] - re-visit GenericResolver
  * [EXTCDI-195] - Rename ClassLevelValidator to ClassLevelConstraintValidator
  * [EXTCDI-199] - re-visit maven id's of dist modules
  * [EXTCDI-201] - Use maven-dependency-plugin instead of shade to produce bundle artifacts
  * [EXTCDI-203] - test compatibility with jboss as v7
