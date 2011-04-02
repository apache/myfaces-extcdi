Release Notes - MyFaces CODI - Version 0.9.4

** Bug

    * [EXTCDI-148] - ViewAccessScope is broken in case of special url mappings
    * [EXTCDI-154] - it should be possible to inject view-access-scoped beans in an access-decision-voter

** Improvement

    * [EXTCDI-152] - support for weld v1.1+
    * [EXTCDI-156] - performance improvements of codi scopes
    * [EXTCDI-157] - base-test-infrastructure should be independent of external implementations
    * [EXTCDI-161] - @ProjectStageActivated support for @JsfPhaseListener

** New Feature

    * [EXTCDI-150] - ViewConfigResolver - resolving view-configs via an api
    * [EXTCDI-155] - optional AccessDecisionVoterContext

** Task

    * [EXTCDI-101] - examples for integration tests
    * [EXTCDI-149] - re-visit view protection
    * [EXTCDI-151] - improve javadoc
    * [EXTCDI-153] - re-visit EditableConversation#deactivate
    * [EXTCDI-158] - workaround for deployment problem in glassfish 3.0.1
    * [EXTCDI-160] - separated cargo support module
