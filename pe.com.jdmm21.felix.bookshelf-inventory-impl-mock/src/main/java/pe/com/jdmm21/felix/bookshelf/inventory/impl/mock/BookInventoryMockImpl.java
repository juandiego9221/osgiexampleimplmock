package pe.com.jdmm21.felix.bookshelf.inventory.impl.mock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import pe.com.jdmm21.felix.bookshelf.inventory.api.Book;
import pe.com.jdmm21.felix.bookshelf.inventory.api.BookAlreadyExistsException;
import pe.com.jdmm21.felix.bookshelf.inventory.api.BookInventory;
import pe.com.jdmm21.felix.bookshelf.inventory.api.BookNotfoundException;
import pe.com.jdmm21.felix.bookshelf.inventory.api.InvalidBookException;
import pe.com.jdmm21.felix.bookshelf.inventory.api.MutableBook;

public class BookInventoryMockImpl implements BookInventory {

	public static final String DEFAULT_CATEGORY = "default";
	private Map<String, MutableBook> booksByIsbn = new HashMap<String, MutableBook>();
	private Map<String, Integer> categories = new HashMap<String, Integer>();

	@Override
	public Set<String> getCategories() {
		return this.categories.keySet();
	}

	@Override
	public Set<String> searchBooks(Map<SearchCriteria, String> criteria) {
		LinkedList<Book> books = new LinkedList<Book>();
		books.addAll(this.booksByIsbn.values());
		for (Map.Entry<SearchCriteria, String> crit : criteria.entrySet()) {
			Iterator<Book> it = books.iterator();
			while (it.hasNext()) {
				Book book = it.next();
				switch (crit.getKey()) {
				case AUTHOR_LIKE:
					if (!checkStringMatch(book.getAuthor(), crit.getValue())) {
						it.remove();
						continue;
					}
					break;
				case ISBN_LIKE:
					if (!checkStringMatch(book.getIsbn(), crit.getValue())) {
						it.remove();
						continue;
					}
					break;
				case CATEGORY_LIKE:
					if (!checkStringMatch(book.getCategory(), crit.getValue())) {
						it.remove();
						continue;
					}
					break;
				case TITLE_LIKE:
					if (!checkStringMatch(book.getTitle(), crit.getValue())) {
						it.remove();
						continue;
					}
					break;
				case RATING_GT:
					if (!checkIntegerGreater(book.getRating(), crit.getValue())) {
						it.remove();
						continue;
					}
					break;
				case RATING_LT:
					if (!checkIntegerSmaller(book.getRating(), crit.getValue())) {
						it.remove();
						continue;
					}
					break;
				default:
					break;
				}
			}
		}
		return null;
	}

	@Override
	public MutableBook createBook(String isbn) throws BookAlreadyExistsException {
		return new MutableBookImpl(isbn);
	}

	@Override
	public String storeBook(MutableBook book) throws InvalidBookException {
		String isbn = book.getIsbn();
		if (isbn == null) {
			throw new InvalidBookException("ISBN is not set");
		}
		this.booksByIsbn.put(isbn, book);
		String category = book.getCategory();
		if (category == null) {
			category = DEFAULT_CATEGORY;
		}
		if (this.categories.containsKey(category)) {
			int count = this.categories.get(category);
			this.categories.put(category, count + 1);
		} else {
			this.categories.put(category, 1);
		}
		return isbn;
	}

	@Override
	public Book loadBook(String isbn) throws BookNotfoundException {
		return loadBookForEdit(isbn);
	}

	@Override
	public MutableBook loadBookForEdit(String isbn) throws BookNotfoundException {
		MutableBook book = this.booksByIsbn.get(isbn);
		if (book == null) {
			throw new BookNotfoundException(isbn);
		}
		return book;
	}

	@Override
	public void removeBook(String isbn) throws BookNotfoundException {
		Book book = this.booksByIsbn.remove(isbn);
		if (book == null) {
			throw new BookNotfoundException(isbn);
		}
		String category = book.getCategory();
		int count = this.categories.get(category);
		if (count == 1) {
			this.categories.remove(category);
		} else {
			this.categories.put(category, count - 1);
		}
	}

	private boolean checkStringMatch(String attr, String critVal) {
		if (attr == null) {
			return false;
		}
		attr = attr.toLowerCase();
		critVal = critVal.toLowerCase();

		boolean startsWith = critVal.startsWith("%");
		boolean endsWith = critVal.endsWith("%");

		if (startsWith && endsWith) {
			if (critVal.length() == 1) {
				return true;
			} else {
				return attr.contains(critVal.substring(1, critVal.length() - 1));
			}
		} else if (startsWith) {
			return attr.endsWith(critVal.substring(1));
		} else if (endsWith) {
			return attr.startsWith(critVal.substring(0, critVal.length() - 1));
		} else {
			return attr.equals(critVal);
		}
	}

	private boolean checkIntegerSmaller(int attr, String critVal) {
		int critValInt;
		try {
			critValInt = Integer.parseInt(critVal);
		} catch (Exception e) {
			return false;
		}
		if (attr <= critValInt) {
			return true;
		}
		return false;
	}

	private boolean checkIntegerGreater(int attr, String critVal) {
		int critValInt;
		try {
			critValInt = Integer.parseInt(critVal);
		} catch (NumberFormatException e) {
			return false;
		}
		if (attr >= critValInt) {
			return true;
		}
		return false;
	}

}
