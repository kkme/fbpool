#!/usr/bin/perl

use LWP::UserAgent;

# system('./getAbbrev.pl');

open(TEAMS, "<../etc/sportsline.abbrev");

my %teams;
while (<TEAMS>) {
    my ($abbrev, $team) = split(/:/, $_);
    chomp $team;
    $teams{$abbrev} = $team;
}
close(TEAMS);

$ua = new LWP::UserAgent;
$ua->agent("AgentName/0.1 " . $ua->agent);

my $req = new HTTP::Request GET => 'http://www.sportsline.com/nfl/scoreboard';

$req->content_type('application/x-www-form-urlencoded');
#$req->content('match=www&errors=0');

$res = $ua->request($req);

if (! $res->is_success) {
    print "Could not get Page\n";
    exit(1);
}

my $data = $res->content;


while ($data =~ /\<table class=\"lineScore(.*?)\<\/table\>/sg) {

    my $gdata = $1;

    # print "GDATA: ".$gdata."\n";

	my $state;
	my $visit;
	my $vscore;
	my $home;
	my $hscore;

    if ($gdata =~ /finalStatus\".*?\"\>(.*?)\<\/td.*?teamLocation\"\>.*?\"\>(.*?)\<\/a\>.*?finalScore\"\>(\d+)\<\/td\>.*?teamLocation\"\>\<a href.*?\"\>(.*?)\<\/a\>.*?finalScore\"\>(\d+)\<\/td\>/sg) {
        $state = $1;
        $visit = $2;
        $vscore= $3;
        $home = $4;
        $hscore = $5;
	}
	elsif ($gdata =~ /gameStatus\"\>(.*?)\<\/td.*?teamLocation\"\>.*?\"\>(.*?)\<\/a\>.*?finalScore\"\>(\d+)\<\/td\>.*?teamLocation\"\>\<a href.*?\"\>(.*?)\<\/a\>.*?finalScore\"\>(\d+)\<\/td\>/sg) {
        $state = $1;
        $visit = $2;
        $vscore= $3;
        $home = $4;
        $hscore = $5;
	}
	else {
		next;
	}

# print "\nGAME [$home ($hscore) vs $visit ($vscore) $state]\n";

        my $gstate = 0;
        my $time = '15:00';
        my $qtr = 0;
        if ($state =~ /OT/) {
            $qtr = 5;
        }
        elsif ($state =~ /1st/) {
            $qtr = 1;
        }
        elsif ($state =~ /2nd/) {
            $qtr = 2;
        }
        elsif ($state =~ /3rd/) {
            $qtr = 3;
        }
        elsif ($state =~ /4th/) {
            $qtr = 4;
        }
        if ($state =~ /Halftime/) {
            $qtr = 2;
            $time = '0:00';
        }
        if ($state =~ /^Final/) {
            if ($qtr != 5) {
                $qtr = 4;
            }
            $time = '0:00';
            $gstate = 1;
        }
        if ($state =~ /(\d+\:\d+)/) {
            $time = $1;
        }
        print "$teams{$home}\t$hscore\t$teams{$visit}\t$vscore\t$time\t$qtr\t$gstate\n";
#exit;
}
# print $res->content;
