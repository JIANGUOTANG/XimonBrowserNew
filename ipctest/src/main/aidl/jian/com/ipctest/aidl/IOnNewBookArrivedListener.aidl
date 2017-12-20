// IOnNewBookArrivedListener.aidl
package jian.com.ipctest.aidl;

// Declare any non-default types here with import statements
import jian.com.ipctest.aidl.Book;
interface IOnNewBookArrivedListener {
    void OnNewBookArrived(in Book book);
}
