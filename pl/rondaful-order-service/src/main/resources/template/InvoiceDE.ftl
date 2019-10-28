<!DOCTYPE html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>Brandslink</title>
</head>
<style>
  * {
    font-size: 10px;
    color: #333;
    padding: 0;
    margin: 0;
  }
  .wraper {
    padding: 12px 20px 8px;
    width: 595px;
    margin:  0 auto;
    background: #fff;
    border: 1px solid #333;
  }
  header {
    padding-bottom: 10px;
    border-bottom: 1px solid #000;
    position: relative;
  }
  .main-title {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
  }
  .main-title p{
    font-size: 18px;
    color: #000;
    border-bottom: 2px solid #000;
    font-weight: bold;
  }
  .left-header {
    font-size: 14px;
    position: absolute;
  }
  .left-header p {
    font-size: 14px;
    font-weight: bold;
    position: absolute;
    bottom: -48px;
  }
  .right-header {
    text-align: right;
  }
  .right-header p + p {
    margin-top: 4px;
  }
  .right-header .item-label{
    margin-right: 4px;
  }
  .item-label,.tabler-header {
    color: #666;
  }
  .item-context {
    margin-left: 81px;
  }
  .content .item-label {
    display: inline-block;
    width: 105px;
    margin-right: 30px;
  }
  .content{
    margin-top: 16px;
  }
  main {
    padding: 0 10px;
  }
  .sub-title {
    font-size: 12px;
    padding-bottom: 4px;
    border-bottom: 1px solid #000;
    margin-bottom: 9px;
    font-weight: bold;
  }
  .content  p + p {
    margin-top: 9px;
  }
  footer {
    padding-top: 8px;
    border-top: 1px solid #000;
    margin-top: 45px;
  }
  footer p {
    font-size: 12px;
    font-weight: 400;
    text-align: center;
  }
  footer p + p {
    margin-top: 4px;
  }
  table {
    width: 100%;
    border-collapse: collapse;
  }
  tr + tr {
    border-top: 1px solid #666;
  }
  th {
    text-align: left;
    border: 1px solid #666;
    padding: 4px 12px;
    font-weight: 400;
  }
  /* .tabler-header{
    font-weight: 400;
  } */
</style>
<body>
<div class="wraper">
  <header>
    <section class="left-header">
      <p>To:${shipToName}</p>
    </section>
    <section class="main-title">
      <p>RECHNUNG</p>
    </section>
    <section class="right-header">
      <p><span class="item-label">DATUM:</span>${date}</p>
      <p><span class="item-label">RECHNUNG #:</span>${createDate}</p>
      <p><span class="item-label">Mehrwertsteuer-Steuernummer:</span>${vatTaxNumber}</p>
    </section>
  </header>
  <main>
    <div class="content">
      <section class="sub-title">Einlieferer</section>
      <p>
        <span class="item-label">Gesellschaft：</span>
        <span class="item-context">${title}</span>
      </p>
      <p>
        <span class="item-label">Telefon：</span>
        <span class="item-context">${telPhone}</span>
      </p>
      <p>
        <span class="item-label">Fax：</span>
        <span class="item-context">${fax}</span>
      </p>
      <p>
        <span class="item-label">URL：</span>
        <span class="item-context">${webSite}</span>
      </p>
      <p>
        <span class="item-label">Adresse：</span>
        <span class="item-context">${countryEnName} ${province} ${city} ${detailAddress}</span>
      </p>
      <p>
        <span class="item-label">Postleitzahl：</span>
        <span class="item-context">${postcode}</span>
      </p>
    </div>

    <div class="content">
      <section class="sub-title">Empfänger</section>
      <p>
        <span class="item-label">Name：</span>
        <span class="item-context">${shipToName}</span>
      </p>
      <p>
        <span class="item-label">Gesellschaft：</span>
        <span class="item-context"></span>
      </p>
      <p>
        <span class="item-label">Adresse：</span>
        <span class="item-context">${shipToCountryName} ${shipToState} ${shipToCity} ${shipToAddrStreet1}</span>
      </p>
      <p>
        <span class="item-label">Telefon：</span>
        <span class="item-context">${shipToPhone}</span>
      </p>
      <p>
        <span class="item-label">Postleitzahl：</span>
        <span class="item-context">${shipToPostalCode}</span>
      </p>
    </div>

    <div class="content">
      <section class="sub-title" style="border:0;margin-bottom:5px;">Produkt</section>
      <table>
        <tr>
          <th class="tabler-header">Order-ID</th>
          <th class="tabler-header">Warehouse SKU</th>
          <th class="tabler-header">Beschreibung</th>
          <th class="tabler-header">Anzahl</th>
          <th class="tabler-header">Price</th>
          <th class="tabler-header">Total</th>
        </tr>
        <#list sysOrderInvoiceExportSkuDetailList as sku>
          <tr>
            <th>${sku.sourceOrderId}</th>
            <th>${sku.sku}</th>
            <th>${sku.itemNameEn}</th>
            <th>${sku.skuQuantity}</th>
            <th>${sku.itemPrice}</th>
            <th>${sku.total}</th>
          </tr>
        </#list>
      </table>
    </div>

    <div class="content">
      <section class="sub-title">Rechnungsbetrag</section>
      <p>
        <span class="item-label">Zwischensumme：</span>
        <span class="item-context">$${subTotal}</span>
      </p>
      <p>
        <span class="item-label">Verschiffen-Gebühr：</span>
        <span class="item-context">$${shippingFee}</span>
      </p>
      <p>
        <span class="item-label">Steuersatz：</span>
        <span class="item-context">${vatTaxRate}%</span>
      </p>
      <p>
        <span class="item-label">Steuern：</span>
        <span class="item-context">$${taxFee}</span>
      </p>
      <p>
        <span class="item-label">Andere：</span>
        <span class="item-context">$${otherFee}</span>
      </p>
      <p>
        <span class="item-label">Total：</span>
        <span class="item-context">$${invoiceTotal}</span>
      </p>
    </div>

  </main>
  <footer>
    <p>Wenn Sie Fragen zu dieser Rechnung haben, kontaktieren Sie bitte</p>
    <p>${contactInfo}</p>
    <p>Danke für Ihr Geschäft!</p>
  </footer>
</div>
</body>
</html>