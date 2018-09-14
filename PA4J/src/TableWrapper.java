
// XXX - Ini.

public class TableWrapper {
	public ClassTable classTable;
	public SymbolTable objectTable;
	public SymbolTable methodTable;

	public TableWrapper(ClassTable classTable, SymbolTable objectTable,
		SymbolTable methodTable) {
		this.classTable = classTable;
		this.objectTable = objectTable;
		this.methodTable = methodTable;
	}

	public TableWrapper(ClassTable classTable) {
		this(classTable, null, null);
	}
}

// XXX - Fin
