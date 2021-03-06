package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ulisboa.tecnico.softeng.tax.dataobjects.InvoiceData;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

import java.util.HashSet;
import java.util.Set;

public class IRS {
	private final Set<TaxPayer> taxPayers = new HashSet<>();
	private final Set<ItemType> itemTypes = new HashSet<>();

	private static IRS instance;

	public static IRS getIRS() {
		if (instance == null) {
			instance = new IRS();
		}
		return instance;
	}

	private IRS() {
	}

	void addTaxPayer(TaxPayer taxPayer) {
		this.taxPayers.add(taxPayer);
	}

	public TaxPayer getTaxPayerByNIF(String NIF) {
		for (TaxPayer taxPayer : this.taxPayers) {
			if (taxPayer.getNIF().equals(NIF)) {
				return taxPayer;
			}
		}
		return null;
	}
	
	public TaxPayer remByReference(String reference) {
		for (TaxPayer taxPayer : this.taxPayers) {
			if (taxPayer.getInvoiceByReference(reference) != null) {
				return taxPayer;
			}
		}
		return null;
	}

	void addItemType(ItemType itemType) {
		this.itemTypes.add(itemType);
	}

	public ItemType getItemTypeByName(String name) {
		
		for (ItemType itemType : this.itemTypes) {

			if (itemType.getName().equals(name)) {
				return itemType;
			}
		}
		return null;
	}

	public static String submitInvoice(InvoiceData invoiceData) {
		IRS irs = IRS.getIRS();
		Seller seller = (Seller) irs.getTaxPayerByNIF(invoiceData.getSellerNIF());
		Buyer buyer = (Buyer) irs.getTaxPayerByNIF(invoiceData.getBuyerNIF());
		ItemType itemType = irs.getItemTypeByName(invoiceData.getItemType());
		
		Invoice invoice = new Invoice(invoiceData.getValue(), invoiceData.getDate(), itemType, seller, buyer);
				
		return invoice.getReference();
	}
	
	public void cancelInvoice(String invoiceReference) {
		int f = 0;
		for (TaxPayer taxPayer : this.taxPayers) {
			Invoice i = taxPayer.getInvoiceByReference(invoiceReference);
			if (i != null) {
				taxPayer.removeInvoice(i);
				f++;
			}
		}
		if (f == 0)
		    throw new TaxException("Nao foi encontrado um Invoice com a referencia dada.");
	}

	public void removeItemTypes() {
		this.itemTypes.clear();
	}

	public void removeTaxPayers() {
		this.taxPayers.clear();
	}

	public void clearAll() {
		removeItemTypes();
		removeTaxPayers();
	}

}
