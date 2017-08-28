#!java
URLClassLoader cl1=(URLClassLoader)getClass().getClassLoader();
URL utab[]=cl1.getURLs();
URL url=new URL(utab[0]+"/data/");
utab=new URL[1];
utab[0]=url;
URLClassLoader cl2=URLClassLoader.newInstance(utab,cl1);
/* find A classe in cl2 namespace \*/
MethodHandles.Lookup lookup=MethodHandles.lookup();
Class a\_cl2=cl2.loadClass("A");
/* find m method of A class */
lookup=lookup.in(a_cl2);
Class ctab[]=new Class[1];
ctab[0]=P.class;
desc=MethodType.methodType(Void.TYPE,ctab);
MethodHandle mh=lookup.findStatic(a\_cl2,"m",desc);//获取m函数的方法句柄
P p = new P();
mh.invokeExact(p);