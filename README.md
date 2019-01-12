# sme-sartopo-mapsrv
small node server for setting gps markers on sartopo-offline

requires node 8.6.0 (for got)

This node server accepts https posts and then uses the sartopo api to set a marker on a given map.  This does not yet work with sartopo as I have not set up the authentication for that.  However, sartopo-offline does not use authentication so works well.  My Sar team uses sartopo-offline during incidents, sartopo.com is not needed.
