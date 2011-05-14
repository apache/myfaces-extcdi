Release Notes - MyFaces CODI - Version 0.9.5

** Bug

    * [EXTCDI-166] - Handle situation for viewRoot = null
    * [EXTCDI-167] - ViewParameter got lost with redirect after post

** Improvement

    * [EXTCDI-173] - optional jsf project-stage

** New Feature

    * [EXTCDI-100] - @ConversationRequired should be supported by typesafe view configs
    * [EXTCDI-164] - optional InvalidBeanCreationEvent
    * [EXTCDI-170] - implementations looked up with CodiUtils#lookupFromEnvironment should be aware of the default implementation

** Task

    * [EXTCDI-162] - re-visit implementation of custom project stages.
    * [EXTCDI-165] - rename #getViewConfig to #getViewConfigDescriptor
    * [EXTCDI-169] - re-visit ClientConfig