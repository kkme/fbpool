#!/usr/bin/perl

=head1 NAME

games.pl - Converts raw schedule from NFL site into format suitable for loadGames

=head1 SYNOPSIS

 games.pl

=head1 OPTIONS

=head1 FILES

The input (via STDIN) is expected to be a file containing the
text cut and pasted via the browser from the schedule page at
nfl.com.  The browser will need to be table-aware when cutting
the selected text.  A sample of the text looks like this:

WEEK 1
Thursday, Sep. 7
GAME    TIME
Miami at Pittsburgh     8:30 p.m.
Sunday, Sep. 10
GAME    TIME
Atlanta at Carolina     1:00 p.m.
Baltimore at Tampa Bay  1:00 p.m.
Buffalo at New England  1:00 p.m.
Cincinnati at Kansas City       1:00 p.m.
Denver at St. Louis     1:00 p.m.
New Orleans at Cleveland        1:00 p.m.
N.Y. Jets at Tennessee  1:00 p.m.
Philadelphia at Houston 1:00 p.m.
Seattle at Detroit      1:00 p.m.
Chicago at Green Bay    4:15 p.m.
Dallas at Jacksonville  4:15 p.m.
San Francisco at Arizona        4:15 p.m.
Indianapolis at N.Y. Giants     8:15 p.m.
Monday, Sep. 11
GAME    TIME
Minnesota at Washington 7:00 p.m.
San Diego at Oakland    10:15 p.m.
...
* Note: One of the Sunday games will move to 8:15 p.m. Sunday night.

There are three important types of lines.  The lines that begin
with "WEEK" are used by the script to determine which week
should be used for subsequent lines.  Lines that consist of
calendar dates are used as the dates for subsequent games.
Finally, the games themselves are used.  No other line has an
affect on the output.

The script will map the "N.Y." and "St." abbreviations to the
style used in the NFL Pool ("NY" and "Saint" respectively).  The
times in the source file are assumed to be given in Eastern time
and are converted to Mountain time.  The year is assumed to be
the current calendar year.  If the month on one of the date
lines is earlier than August, the game is assumed to be played
the following year (though the first column of the output still
properly reflects the current year).  The script properly handles
January games and games scheduled in the morning or over the noon
hour.

Here is the converted result from the previous sample:

2006    1       Miami   Pittsburgh      0       0       9/7/06 18:30
2006    1       Atlanta Carolina        0       0       9/10/06 11:00
2006    1       Baltimore       Tampa Bay       0       0       9/10/06 11:00
2006    1       Buffalo New England     0       0       9/10/06 11:00
2006    1       Cincinnati      Kansas City     0       0       9/10/06 11:00
2006    1       Denver  Saint Louis     0       0       9/10/06 11:00
2006    1       New Orleans     Cleveland       0       0       9/10/06 11:00
2006    1       NY Jets Tennessee       0       0       9/10/06 11:00
2006    1       Philadelphia    Houston 0       0       9/10/06 11:00
2006    1       Seattle Detroit 0       0       9/10/06 11:00
2006    1       Chicago Green Bay       0       0       9/10/06 14:15
2006    1       Dallas  Jacksonville    0       0       9/10/06 14:15
2006    1       San Francisco   Arizona 0       0       9/10/06 14:15
2006    1       Indianapolis    NY Giants       0       0       9/10/06 18:15
2006    1       Minnesota       Washington      0       0       9/11/06 17:00
2006    1       San Diego       Oakland 0       0       9/11/06 20:15

=head1 EXAMPLE

cat games0600.raw | ./games.pl > games0600.txt

=cut

use strict;

my %months = ('Jan' => 1,
              'Feb' => 2,
              'Mar' => 3,
              'Apr' => 4,
              'May' => 5,
              'Jun' => 6,
              'Jul' => 7,
              'Aug' => 8,
              'Sep' => 9,
              'Oct' => 10,
              'Nov' => 11,
              'Dec' => 12);

my %teams = ('Arizona' => 'Arizona',
             'Atlanta' => 'Atlanta',
             'Baltimore' => 'Baltimore',
             'Buffalo' => 'Buffalo',
             'Carolina' => 'Carolina',
             'Chicago' => 'Chicago',
             'Cincinnati' => 'Cincinnati',
             'Cleveland' => 'Cleveland',
             'Dallas' => 'Dallas',
             'Denver' => 'Denver',
             'Detroit' => 'Detroit',
             'Green Bay' => 'Green Bay',
             'Houston' => 'Houston',
             'Indianapolis' => 'Indianapolis',
             'Jacksonville' => 'Jacksonville',
             'Kansas City' => 'Kansas City',
             'Miami' => 'Miami',
             'Minnesota' => 'Minnesota',
             'New England' => 'New England',
             'New Orleans' => 'New Orleans',
             'N.Y. Giants' => 'NY Giants',
             'N.Y. Jets' => 'NY Jets',
             'Oakland' => 'Oakland',
             'Philadelphia' => 'Philadelphia',
             'Pittsburgh' => 'Pittsburgh',
             'St. Louis' => 'Saint Louis',
             'San Diego' => 'San Diego',
             'San Francisco' => 'San Francisco',
             'Seattle' => 'Seattle',
             'Tampa Bay' => 'Tampa Bay',
             'Tennessee' => 'Tennessee',
             'Washington' => 'Washington');

my $week = 0;
my $year = (localtime)[5] + 1900;
my $month = 0;
my $day = 0;
my $hour = 0;
my $yr = '00';

my $visitor = 'visitor';
my $home = 'home';

while (<>) {
  my $line = $_;
  if ($line =~ /WEEK (\d+)/) {
    #print "WEEK $1\n";
    $week = $1;
  } elsif ($line =~ /(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday), (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\. (\d+)/) {
    #print "$1, $2. $3\n";
    $month = $months{$2};
    $day = $3;
  } elsif ($line =~ /(.*) at (.*)\t(\d+):(\d+) (a\.m\.|p\.m\.)/) {
    #print "$1 at $2\t$3:$4 $5\n";
    $hour = ($3 == 12 ? 0 : $3) + ($5 eq 'p.m.' ? 10 : -2);
    $yr = sprintf "%.2d", (($month < 8 ? $year + 1 : $year) % 100);
    print "$year\t$week\t$teams{$1}\t$teams{$2}\t0\t0\t$month/$day/$yr $hour:$4\n";
  }
}

