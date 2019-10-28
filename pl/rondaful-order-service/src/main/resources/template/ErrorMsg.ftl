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
  <main>
    <div class="content">
      <p>${msg}</p>
    </div>
  </main>
</div>
</body>
</html>