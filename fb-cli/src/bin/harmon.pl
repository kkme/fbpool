#!/usr/bin/perl

use LWP::UserAgent;

my %teams = (
	"TEN", "Tennessee",
	"PIT", "Pittsburgh",
	"MIA", "Miami",
	"ATL", "Atlanta",
	"KC", "Kansas City",
	"BAL", "Baltimore",
	"PHI", "Philadelphia",
	"CAR", "Carolina",
	"DEN", "Denver",
	"CIN", "Cincinnati",
	"MIN", "Minnesota",
	"CLE", "Cleveland",
	"NYJ", "NY Jets",
	"HOU", "Houston",
	"JAC", "Jacksonville",
	"IND", "Indianapolis",
	"DET", "Detroit",
	"NO", "New Orleans",
	"DAL", "Dallas",
	"TB", "Tampa Bay",
	"SF", "San Francisco",
	"ARI", "Arizona",
	"WAS", "Washington",
	"NYG", "NY Giants",
	"STL", "Saint Louis",
	"SEA", "Seattle",
	"CHI", "Chicago",
	"GB", "Green Bay",
	"BUF", "Buffalo",
	"NE", "New England",
	"SD", "San Diego",
	"OAK", "Oakland"
	);

my %picks;

$ua = new LWP::UserAgent;
$ua->agent("AgentName/0.1 " . $ua->agent);

my $req = new HTTP::Request GET => 'http://www.cbssports.com/nfl/features/writers/harmon/forecast';

$req->content_type('application/x-www-form-urlencoded');
#$req->content('match=www&errors=0');

$res = $ua->request($req);

if (! $res->is_success) {
    print "Could not get Page\n";
    exit(1);
}

my $data = $res->content;


#while ($data =~ /div class\=SLTables1\>(.*?)\<\/div\>/sg) {
while ($data =~ /\<tr  id=special(.*?)span\>\<\/td\>\<\/tr\>/sg) {

    my $gdata = $1;

#print "GDATA: ".$gdata."\n";


    if ($gdata =~ /icons\/(.*?)\.gif.*?24px\'\>(.*?)\<\/span.*?24px\'\>(.*?)\</sg) {
        my $pick = $1;
        my $winScore = $2;
        my $loseScore = $3;
		my $spread = $winScore - $loseScore;
#print "\nGAME [$pick ($winScore - $loseScore = $spread)]\n";
	
		$picks{$pick} = $spread;
    }
}

my $now = localtime time;

print "\n$now\n\n\~Harmon Forcast\n\$\n";
foreach $value (sort {$picks{$b} <=> $picks{$a} } keys %picks) {
	#print "$value \t$picks{$value}\n";
	#print "$teams{$value} \t$picks{$value}\n";
	print "$teams{$value}\n";
	#print "$teams{$value}\n";
}
print "\n";

# print $res->content;
