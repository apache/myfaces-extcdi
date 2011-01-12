Release Notes - MyFaces CODI - Version 0.9.2

** Bug
    * [EXTCDI-28] - CodiFacesContextWrapper in some scenarios throws a NPE because BeanManagerProvider hasn't yet been initialized
    * [EXTCDI-49] - @ViewAccessScoped in combination with h:link and back-buttons
    * [EXTCDI-92] - ConversationUtils.cacheWindowId() ignores session invalidation
    * [EXTCDI-96] - fallback of SecurityViewListener
    * [EXTCDI-108] - custom formatter config for other locales

** Improvement
    * [EXTCDI-72] - support for view-config inheritance in case of nested interfaces
    * [EXTCDI-89] - spi for view-configs
    * [EXTCDI-94] - codi navigation handlers should delegate navigation in case of unhandled exceptions
    * [EXTCDI-95] - unified usage of @Advanced
    * [EXTCDI-97] - provide a small JavaScript library to deorate hrefs with the windowhandler cookie logic
    * [EXTCDI-99] - replace codi config interfaces with the default implementation
    * [EXTCDI-102] - InstanceProducer should only produce Editable* artifacts
    * [EXTCDI-104] - testing support for ViewConfigCache
    * [EXTCDI-109] - produced bv artifacts should be serializable
    * [EXTCDI-113] - log module configurations after bootstrapping codi
    * [EXTCDI-115] - navigation to the default error view
    * [EXTCDI-117] - [jsf 2.1 support] forward to the wrapped navigation-case map for write operations

** New Feature
    * [EXTCDI-18] - Provide ProjectStage determination mechanisms also for non-JSF environments like unit tests
    * [EXTCDI-93] - @PostRenderView
    * [EXTCDI-103] - support for injecting java.util.logging.Logger
    * [EXTCDI-106] - dependency injection support for junit tests
    * [EXTCDI-110] - support for invalidValue as implicit key in bv violation messages
    * [EXTCDI-112] - startup event
    * [EXTCDI-114] - manual navigation via ViewNavigationHandler
    * [EXTCDI-116] - keep faces messages per defaut

** Task
    * [EXTCDI-37] - refactoring of ProjectStageProducer
    * [EXTCDI-91] - src-assemblies are lost during release procedure and bin-asseblies are renamed
    * [EXTCDI-105] - create cargo-test-infrastructure module
    * [EXTCDI-107] - surefire config for junit
