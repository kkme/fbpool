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

my $req = new HTTP::Request GET => 'http://scores.covers.com/football-scores-matchups.aspx?t=0';

$req->content_type('application/x-www-form-urlencoded');
#$req->content('match=www&errors=0');

$res = $ua->request($req);

if (! $res->is_success) {
    print "Could not get Page\n";
    exit(1);
}

my $data = $res->content;


while ($data =~ /\<table(.*?)table\>/sg) {

    my $gdata = $1;

#print "GDATA: ".$gdata."\n";

    if ($gdata =~ /Odds.*?_parent\"\>(.*?) .*?total_top1.*?\>(.*?)\<.*?_parent\"\>(.*?) .*?spread_bottom1.*?\>(.*?)\</sg) {
        my $visit = $1;
        my $overUnder = $2;
        my $home = $3;
        my $spread = $4;
#print "\nGAME [$home vs $visit ($spread)]\n";
	
		if ($spread < 0) {
			$picks{$home} = $spread * -1;
		}
		elsif ($spread == 0) {
			$picks{$home} = 0;
		}
		else {
			$picks{$visit} = $spread;
		}
    }
#exit;
}

#foreach $key (keys %picks) {
my $now = localtime time;

print "\n$now\n\n\~Vegas\n\$\n";
foreach $value (sort {$picks{$b} <=> $picks{$a} } keys %picks) {
	print "$teams{$value}\n";
	#print "$teams{$value} \t$picks{$value}\n";
	#print "$teams{$value}\n";
}
print "\n";

# print $res->content;
