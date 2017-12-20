// IBookManager.aidl
package jian.com.ipctest.aidl;

// Declare any non-default types here with import statements
import jian.com.ipctest.aidl.Book;
import jian.com.ipctest.aidl.IOnNewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void regiesterListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}
