# VisibleHand

VisibleHand is a system for tracking personal resource usage. Currently this means scraping an email inbox for airline flight receipts and then 
using a variety of data sources (airline timetables, aircraft fuel models and seating charts) to infer carbon dioxide emisions data.

## Usage
To test VisibleHand you will need Java and Maven installed. First clone the repository

    git clone https://github.com/potash/visiblehand.git
then enter the directory

    cd visiblehand
and compile the code

    mvn clean compile
and finally execute it

    mvn exec:java
When prompted enter you username and password. The program will search your Inbox for flight receipts and output a lot of information about fuel consumption.

## Data
The basic data about airlines, airports and routes comes from [OpenFlights](http://openflights.org/data.html).

The equipment data (aircraft names and ICAO and IATA identifiers) were mostly parsed from [avcodes.co.uk](http://www.avcodes.co.uk/acrtypes.asp). Missing entries were added manually. Aggregate equipment data was generated using ad hoc code that is not currently in the repository.

The majority of the fuel data comes from the European Environmental Agency's 2013 Air Pollutant Emission Inventory Guidebook. A few additional aircraft were added from data available in the [TEAM Aero operator guidebooks](http://www.team.aero/controls/aviationdata/index.php).

Seating data for various airlines come from custom web scrapers that are messy and not currently in the repository.

Currently all of this data is in csv files in the repository. It would make sense to write code that pulls this data in from the original sources.
