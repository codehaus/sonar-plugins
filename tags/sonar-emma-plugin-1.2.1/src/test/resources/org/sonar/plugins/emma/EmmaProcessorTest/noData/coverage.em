EMMA                 �      /     0org/xdoclet/AbstractJavaGeneratingPluginTestCase org/xdoclet $AbstractJavaGeneratingPluginTestCase�%����?% )AbstractJavaGeneratingPluginTestCase.java    <init> ()V                   (   ( compare (Ljava/net/URL;Ljava/net/URL;)V                   +   -   ,   + toCharArray (Ljava/io/Reader;)[C                            1   0      3      4      6   0 createMetadataProvider !()Lorg/generama/MetadataProvider;                   :   : getTestSource ()Ljava/net/URL;                   >   > ,org/xdoclet/tools/SystemQDoxPropertyExpander org/xdoclet/tools SystemQDoxPropertyExpandero-�cď)� SystemQDoxPropertyExpander.java    <init> ()V                          expandProperty 9(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;                                            "    ,org/xdoclet/ant/XDocletTask$PropertyComposer org/xdoclet/ant XDocletTask$PropertyComposer'��tM�B XDocletTask.java    <init> c(Lorg/xdoclet/tools/SystemQDoxPropertyExpander;Lorg/xdoclet/tools/PropertiesQDoxPropertyExpander;)V                   ^   ]   \   [   X   [ expand &(Ljava/lang/String;)Ljava/lang/String;                                  a      a      b   e      f      h      j   a org/xdoclet/ant/XDocletTask$1 org/xdoclet/ant XDocletTask$1>�֕ XDocletTask.java    <init> B(Lorg/xdoclet/ant/XDocletTask;Ljava/lang/Class;Ljava/lang/Class;)V                   $   $ composeContainer =(Lorg/picocontainer/MutablePicoContainer;Ljava/lang/Object;)V                   *   )   (   '   &   /   & org/xdoclet/ant/XDocletTask org/xdoclet/ant XDocletTask��#4�� XDocletTask.java    <init> ()V                   W               !    createGenerama ()Lorg/generama/Generama;                                     $      $      $      $      $      $      $   $ 
addFileset '(Lorg/apache/tools/ant/types/FileSet;)V                   5   4   4 setEncoding (Ljava/lang/String;)V                   9   8   8 
setVerbose (Ljava/lang/Boolean;)V                   =   <   < addProperties (Lorg/xdoclet/ant/Properties;)V                               @      A      E      F      K   J   @ class$ %(Ljava/lang/String;)Ljava/lang/Class;    0org/xdoclet/tools/PropertiesQDoxPropertyExpander org/xdoclet/tools PropertiesQDoxPropertyExpander�����6� #PropertiesQDoxPropertyExpander.java    <init> ()V                             addProperties +(Ljava/lang/String;Ljava/util/Properties;)V                            !      !      "      &   %   ! 
getPattern ()Ljava/lang/String;                   .   . expandProperty 9(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;                         :   8   7      ;      >   7  org/xdoclet/QDoxMetadataProvider org/xdoclet QDoxMetadataProvider|hW�î$ QDoxMetadataProvider.java    <init> (Ljava/io/File;)V          	               )   (      *      0   /   .   ( <init> 3(Ljava/io/File;Lorg/xdoclet/QDoxPropertyExpander;)V          
               8   7      9      @   >   =   7 <init> #(Lorg/xdoclet/JavaSourceProvider;)V                   G   F   F <init> 6(Lorg/xdoclet/JavaSourceProvider;Ljava/lang/Boolean;)V                   L   P   O   N   M   L <init> E(Lorg/xdoclet/JavaSourceProvider;Lorg/xdoclet/QDoxPropertyExpander;)V                   W   X   W <init> X(Lorg/xdoclet/JavaSourceProvider;Lorg/xdoclet/QDoxPropertyExpander;Ljava/lang/Boolean;)V                   b   a   `   _   ^   d   ^ <init> (Ljava/net/URL;)V                   k   j   j <init> $(Ljava/net/URL;Ljava/lang/Boolean;)V                   q   z   q  addSourcesFromJavaSourceProvider )(Lcom/thoughtworks/qdox/JavaDocBuilder;)V             	                  ~            �   �   �      �   ~ getDocletTagFactory -()Lorg/generama/ConfigurableDocletTagFactory;                   �   � getExpander $()Lorg/xdoclet/QDoxPropertyExpander;                   �   � getMetadata ()Ljava/util/Collection;             
            	            �   �   �      �   �      �      �      �      �   �   �   �      �   �   � getOriginalFileName &(Ljava/lang/Object;)Ljava/lang/String;                            �      �   �   �      �      �   � getOriginalPackageName &(Ljava/lang/Object;)Ljava/lang/String;                         �      �   �      �   � start ()V                   �   � stop ()V                         �      �      �   � 0org/xdoclet/ExpanderConfigurableDocletTagFactory org/xdoclet $ExpanderConfigurableDocletTagFactory�}Fld:� )ExpanderConfigurableDocletTagFactory.java    <init> %(Lorg/xdoclet/QDoxPropertyExpander;)V                            7         (   '   2   0   -      2      3      3      3      3   2      6   7   - createDocletTag M(Ljava/lang/String;Ljava/lang/String;)Lcom/thoughtworks/qdox/model/DocletTag;                   :   : createDocletTag �(Ljava/lang/String;Ljava/lang/String;Lcom/thoughtworks/qdox/model/AbstractBaseJavaEntity;I)Lcom/thoughtworks/qdox/model/DocletTag;       *                                          A                           
         	                           
               
         *      >      ?      A   D   B      E      E      E      F   E      I      I      I      I      I      I      I      I      I      I      I      I      I      I      I      I      I      I      I      I      I      U   I   M      U      W   Z   X      [      ^      _   o      d   c      f   e      h   g      j   i      l   k      m   n      U      s   > registerTag &(Ljava/lang/String;Ljava/lang/Class;)V                                        �      �      �      �      �      �      �      �   �   � registerTags (Ljava/util/Map;)V                            �   �      �      �   �   �      �   � getUnknownTags ()Ljava/util/List;                   �   � printUnknownTags ()V                            �      �      �   �   �      �   � class$ %(Ljava/lang/String;)Ljava/lang/Class;    $org/xdoclet/ModelCheckerTagFactory$1 org/xdoclet ModelCheckerTagFactory$1;�D�M}:a ModelCheckerTagFactory.java    <init> '(Lorg/xdoclet/ModelCheckerTagFactory;)V                   0   0 execute (Ljava/lang/Object;)V                   4   3   2   2 org/xdoclet/predicate/HasTag org/xdoclet/predicate HasTag��ܺmJ HasTag.java    <init> ()V                             <init> :(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V                                         
setTagName (Ljava/lang/String;)V                          setAttributeName (Ljava/lang/String;)V                       !     setAttributeValue (Ljava/lang/String;)V                   %   $   $ setSuperclasses (Z)V                   )   (   ( evaluate (Ljava/lang/Object;)Z                                              	               .   -   ,      /      3   2      4      6      6      8      8      8      8      9      9      ;      =      A   , "org/xdoclet/ModelCheckerTagFactory org/xdoclet ModelCheckerTagFactory�.t{��� ModelCheckerTagFactory.java    <init> .(Lorg/generama/ConfigurableDocletTagFactory;)V                                createDocletTag M(Ljava/lang/String;Ljava/lang/String;)Lcom/thoughtworks/qdox/model/DocletTag;                	             !      "      $     createDocletTag �(Ljava/lang/String;Ljava/lang/String;Lcom/thoughtworks/qdox/model/AbstractBaseJavaEntity;I)Lcom/thoughtworks/qdox/model/DocletTag;                         )   (      *      ,   ( validateModel ()V                   6   0   0 org/xdoclet/predicate/IsA org/xdoclet/predicate IsA1@���8� IsA.java    <init> ()V                          <init> (Ljava/lang/String;)V                             setClassName (Ljava/lang/String;)V                          evaluate (Ljava/lang/Object;)Z                          .org/xdoclet/tools/AbstractQDoxPropertyExpander org/xdoclet/tools AbstractQDoxPropertyExpander&��C��� !AbstractQDoxPropertyExpander.java    <init> ()V                             
getPattern ()Ljava/lang/String;                   %   % expandProperty 9(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;    expand &(Ljava/lang/String;)Ljava/lang/String;       
                  
               
      5   4   8   7      <      ?   =      ?      @   ?      G   E   D   C      H      J      N   M      S   R   4 org/xdoclet/XDocletTag org/xdoclet 
XDocletTag�gz�.�6 XDocletTag.java    <init> \(Ljava/lang/String;Ljava/lang/String;Lcom/thoughtworks/qdox/model/AbstractBaseJavaEntity;I)V                   )   (   ( <init> ~(Ljava/lang/String;Ljava/lang/String;Lcom/thoughtworks/qdox/model/AbstractBaseJavaEntity;ILorg/xdoclet/QDoxPropertyExpander;)V                                                             .   -      .      .      .      1   0   /      1      1      2   1      2      2      2      2      3      5      9   8   7   - validateLocation ()V    bomb (Ljava/lang/String;)V                    >   > isOnConstructor ()Z                   E   E setOnConstructor (Z)V                   J   I   I 
isOnMethod ()Z                   M   M setOnMethod (Z)V                   R   Q   Q 	isOnField ()Z                   U   U 
setOnField (Z)V                   Z   Y   Y 	isOnClass ()Z                   ]   ] 
setOnClass (Z)V                   b   a   a validateModel ()V    getNamedParameterMap ()Ljava/util/Map;                                  h      k   i      m   l      m      p   o   n      s   h getParameters ()[Ljava/lang/String;                                  w      x   z      {      {      |   {      �   w class$ %(Ljava/lang/String;)Ljava/lang/Class;    <org/xdoclet/ant/XDocletTask$MyPropertiesQDoxPropertyExpander org/xdoclet/ant ,XDocletTask$MyPropertiesQDoxPropertyExpanderذ���*t XDocletTask.java    <init>  (Lorg/xdoclet/ant/XDocletTask;)V                   	         P   O      P      S   R   Q      T   O org/xdoclet/ant/Properties org/xdoclet/ant 
PropertiesIR�+�� Properties.java    <init> ()V                          getFile ()Ljava/lang/String;                       setFile (Ljava/lang/String;)V                          getId ()Ljava/lang/String;                         setId (Ljava/lang/String;)V                   %   $   $ getProperties ()Ljava/util/Properties;                      +   *   )      -   ,   ) "org/xdoclet/QDoxMetadataProvider$1 org/xdoclet QDoxMetadataProvider$1k�\y^� QDoxMetadataProvider.java    <init> (Ljava/net/URL;)V                   q   q getEncoding ()Ljava/lang/String;                   s   s getURLs ()Ljava/util/Collection;                   w   w !org/xdoclet/ant/AntSourceProvider org/xdoclet/ant AntSourceProvider2�:乧"� AntSourceProvider.java    <init> I(Ljava/util/Collection;Lorg/apache/tools/ant/Project;Ljava/lang/String;)V                                   getURLs ()Ljava/util/Collection;       	                              	                         #   "   !      #      *   '   %   $      )   (      #      ,      -    getEncoding ()Ljava/lang/String;                   1   1 org/xdoclet/XDoclet org/xdoclet XDoclet�<0��� XDoclet.java    <init> %(Ljava/lang/Class;Ljava/lang/Class;)V                                                        composeContainer =(Lorg/picocontainer/MutablePicoContainer;Ljava/lang/Object;)V          
                   class$ %(Ljava/lang/String;)Ljava/lang/Class;   