Release Notes - MyFaces CODI - Version 0.9.0

** Bug
    * [EXTCDI-22] - make ProjectStageProducer classloader aware
    * [EXTCDI-24] - issue with MessageContextAware in a multithreaded environment
    * [EXTCDI-26] - CDI Extensions should be disabled by default
    * [EXTCDI-29] - change myfaces-api and myfaces-impl dependency scopes to 'provided'
    * [EXTCDI-30] - BeanManagerProvider is not shared classloader capable
    * [EXTCDI-34] - PhaseListenerExtension is not Multi-WebApp ClassLoader safe
    * [EXTCDI-38] - @ViewAccessScoped creates new conversation context each time it get's involked via GET
    * [EXTCDI-42] - DefaultWindowContextManager#redirect drops all viewParams
    * [EXTCDI-47] - missing possibility to order annotated @JsfPhaseListeners
    * [EXTCDI-48] - The InitialRedirect which adds a windowId must not be defered for redirect responses
    * [EXTCDI-50] - @ViewAccessScoped annotation must not be used as Qualifier
    * [EXTCDI-69] - CODI sometimes locks up if started under heavy load

** Improvement
    * [EXTCDI-27] - shade extcdi-jsf12 artifact into extcdi-jsf20 artifact
    * [EXTCDI-31] - switch to geronimo validation API as default
    * [EXTCDI-35] - @View should support typesafe view configs
    * [EXTCDI-39] - Integer.MAX_VALUE for MAX_WINDOW_CONTEXT_COUNT_DEFAULT in case of ProjectStage.SystemTest
    * [EXTCDI-40] - unused window-context instances should be removed asap
    * [EXTCDI-43] - allow given window-ids
    * [EXTCDI-52] - names for beans provided by codi should be defined centrally
    * [EXTCDI-63] - optional bean, conversation and window events
    * [EXTCDI-70] - "enhanced" qualifier for add-ons
    * [EXTCDI-71] - ClassDeactivator configuration via JNDI

** New Feature
    * [EXTCDI-1] - (grouped) conversation scope
    * [EXTCDI-2] - view access scope
    * [EXTCDI-3] - window scope
    * [EXTCDI-4] - transactional annotation
    * [EXTCDI-6] - typesafe configurations
    * [EXTCDI-8] - producers for bean-validation artifacts
    * [EXTCDI-12] - jsf lifecycle phase information
    * [EXTCDI-14] - Implement support for @ViewScoped as CDI context
    * [EXTCDI-20] - cdi aware bv constraint validators
    * [EXTCDI-21] - i18n aware messages
    * [EXTCDI-23] - faces-request interceptors
    * [EXTCDI-36] - navigation via typesafe view config
    * [EXTCDI-46] - request-lifecycle callbacks via view configs
    * [EXTCDI-51] - allow to manually force a new windowId for links
    * [EXTCDI-54] - inline-script evaluation
    * [EXTCDI-58] - support for @Inject in converters, validators and phase-listeners
    * [EXTCDI-59] - @Secured for secured actions and beans
    * [EXTCDI-60] - @Secured for secured view configs
    * [EXTCDI-61] - support for observer methods for system-events
    * [EXTCDI-64] - @ViewMetaData for custom meta-data for view configs
    * [EXTCDI-65] - @CloseConversationGroup
    * [EXTCDI-66] - deactivatable default implementations
    * [EXTCDI-67] - jsf2 scopes should be mapped to cdi scopes automatically
    * [EXTCDI-68] - view-controller annotations based on view-config

** Task
    * [EXTCDI-7] - module structure
    * [EXTCDI-33] - conversations: performance improvements
    * [EXTCDI-41] - introduce WindowContextQuotaHandler
    * [EXTCDI-45] - myfaces codi as shared lib
    * [EXTCDI-53] - eval producers for all config entries
    * [EXTCDI-57] - revisit Conversation#end
