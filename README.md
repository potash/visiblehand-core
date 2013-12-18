visiblehand-core
===============
VisibleHand is a system for tracking personal resource usage.

Currently this means scraping an email inbox for airline flight receipts and utility e-bills and then 
using a variety of data (airline timetables, aircraft fuel models, seating charts, utility prices, electricity generation) to infer carbon dioxide emisions data.

## Usage
See [visiblehand-cli](https://github.com/potash/visiblehand-cli)

## Data
Currently all of the data is in csv files in the repository. It would make sense to programmatically fetch and process the data in from the original sources.

### Air travel
The basic data about airlines, airports and routes comes from [OpenFlights](http://openflights.org/data.html).

The equipment data (aircraft names and ICAO and IATA identifiers) were mostly parsed from [avcodes.co.uk](http://www.avcodes.co.uk/acrtypes.asp). Missing entries were added manually. Aggregate equipment data was generated using ad hoc code that is not currently in the repository.

The majority of the fuel data comes from the European Environmental Agency's 2013 Air Pollutant Emission Inventory Guidebook. A few additional aircraft were added from data available in the [TEAM Aero operator guidebooks](http://www.team.aero/controls/aviationdata/index.php).

Seating data for various airlines come from custom web scrapers that are messy and not currently in the repository.

### Utilities
Electricity emissions data comes from the EPA's [eGrid](http://www.epa.gov/cleanenergy/documents/egridzips/Power_Profiler_Zipcode_Tool_v4-1.xlsx). The electricity cost and natural gas price data is fetched from the EIA's [API](http://www.eia.gov/beta/api/).
