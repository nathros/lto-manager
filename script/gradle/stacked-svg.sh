#! /bin/bash

set -e # Exit on error

# https://simurai.com/blog/2012/04/02/svg-stacks
# This script pulls multiple icons into single SVG
# Each icon will not be visible until the an anchor is used
# Example: icon-pack.svg#user

# This is fed by icons.json:
# {
#		"@anchor-name" : {
#			"@theme-name-1" : "@svg-path",
#			"@theme-name-2" : "@svg-path"		
# 		}	
# }

# @anchor-name : The anchor name to show this icon 
# @theme-name  : The theme name (stacked SVG file), must exist in THEMES (below)
# @svg-path    : File path to the SVG 
if [[ $1 = "icon" ]]; then
	THEMES=(tabler remix)
	THEMES_DIR=(tabler-icons RemixIcon)
	THEMES_VIEWBOX=('viewBox="0 0 24 24"' 'viewBox="0 0 24 24"')
	JSON=icons.json
elif [[ $1 == "filemanager" ]]; then
	THEMES=(breeze kora)
	THEMES_DIR=(breeze-icons kora)
	THEMES_VIEWBOX=('viewBox="0 0 64 64"' 'viewBox="0 0 48 48"')
	JSON=filemanager.json
else
	echo -e "\e[1;31mError: \e[0;41mMissing operation\e[0m\e[1;31m can accept \e[0;41micon\e[0m\e[1;31m or \e[0;41mfilemanager\e[0m"
	exit 1
fi
# Limitation: the viewbox for all icons in a theme must be the same

cd "$(dirname "$0")"
SVG_ANCHOR=""
CHECK_THEMES=""
ROOT_DIR="../../"
EXT_PATH=external/
HTML_PREVIEW="${1}.preview.html"

echo "Fetching submodules at: ${ROOT_DIR}${EXT_PATH}"
echo "Very first run may take a few minutes..."
git submodule update --progress --init --recursive

echo "<!DOCTYPE html><html><body>" > $HTML_PREVIEW
echo "<style>td, th { border: 1px solid; } td { padding: 4px;} table { border-collapse: collapse; } tr th { position: sticky; top: 0; background-color: white; }</style>" >> $HTML_PREVIEW
echo "<style>img { width: 5rem; height: 5rem; } .fill { background-color: lightgrey } a { text-decoration: none; }</style>" >> $HTML_PREVIEW
echo "<table>" >> $HTML_PREVIEW
echo -n "<tr><th>Icon Anchor</th>" >> $HTML_PREVIEW

echo Using $1 themes:
printf "%-7s | %-22s | %-50s | %s" "Theme" "Path" "Repo" "ViewBox"
echo
for i in "${!THEMES[@]}"
do
	GIT_REPO=$(cat ../../.git/config | grep ${THEMES_DIR[$i]})
	GIT_REPO=${GIT_REPO#*=}
	TMP=`echo "${THEMES_VIEWBOX[$i]}" | cut -d'"' -f 2`
	printf "%-7s | %-22s | %-50s | %s\n" ${THEMES[$i]} ${EXT_PATH}${THEMES_DIR[$i]} $GIT_REPO "$TMP"

	echo -n "<th>${THEMES[$i]} <a href='${GIT_REPO}' target='_blank'>&#128279;</a></th>" >> $HTML_PREVIEW
	# Create stacked SVG file
	echo "<svg xmlns=\"http://www.w3.org/2000/svg\" ${THEMES_VIEWBOX[$i]}>" > "${THEMES[$i]}.svg"
	echo "<defs><style>svg .icon { display: none } svg .icon:target { display: inline }</style></defs>" >> "${THEMES[$i]}.svg"
done
echo "</tr>" >> $HTML_PREVIEW

# Some SVGs use the same internal id='' which causes issue when combined
UNIQUE_ID=0 # Used for changing SVG id
THEME_INDEX=0

while IFS= read -r line; do
	# echo "Line: $line"

	# Skip first line
	if [[ $line = '{' ]]; then
		continue
	fi
	# Skip last line
	if [[ $line = '}' ]]; then
		continue
	fi
	
	# Start of new theme
	if [[ $line = *'{' ]]; then
		CHECK_THEMES="${THEMES[@]}" # Set themes list
		SVG_ANCHOR=`echo "$line" | cut -d'"' -f 2`
		THEME_INDEX=0
		# echo Processing: $SVG_ANCHOR
		echo -n "<tr><td>$SVG_ANCHOR</td>" >> $HTML_PREVIEW
		continue
	fi

	# Icon theme line
	if [[ $line = *'"'* ]]; then
		CURRENT_THEME=`echo "$line" | cut -d'"' -f 2`
		CURRENT_PATH=`echo "$line" | cut -d'"' -f 4`
		#echo "$CURRENT_THEME : $CURRENT_PATH"

		if [ ! -f "${ROOT_DIR}${CURRENT_PATH}" ]; then
			echo "File not found! $CURRENT_PATH"
			echo -e "\e[1;31mError: \e[0;41m$CURRENT_PATH\e[0m\e[1;31m file does not exist for theme \e[0;41m$CURRENT_THEME\e[0m"
			echo "Check file: ${EXT_PATH}$JSON"
			exit 1
		fi

		# Remove current from list
		CHECK_THEMES=( "${CHECK_THEMES[@]/$CURRENT_THEME}" )

		echo -n "<td>" >> $HTML_PREVIEW
		echo -n "<img src='./$CURRENT_THEME.svg#$SVG_ANCHOR'>" >> $HTML_PREVIEW
		echo -n "<img class='fill' src='./$CURRENT_THEME.svg#$SVG_ANCHOR'>" >> $HTML_PREVIEW
		echo -n "</td>" >> $HTML_PREVIEW
		ICON=$(cat "${ROOT_DIR}${CURRENT_PATH}")

		# Some icons have duplicate ids, these need to be replaced
		FOUND_ID_FULL_STR=$(echo $ICON | grep -o 'id\=\"\w*\"' | cat) # Find matches of: id="id-value"
		while IFS= read -r ID_ATTR; do
			# Get value of id
			FOUND_ID_VALUE=`echo "$ID_ATTR" | cut -d'"' -f 2`
			NEW_ID="_${1}_$UNIQUE_ID"
			# echo "Found id str  : $ID_ATTR"
			# echo "Found id value: $FOUND_ID_VALUE"
			# echo "New id value  : $NEW_ID"
			ICON=${ICON/$ID_ATTR/id=\"_${1}_$UNIQUE_ID\"} # Replace id declaration
			ICON=${ICON//#$FOUND_ID_VALUE/#$NEW_ID}       # Replace id use

			((UNIQUE_ID=UNIQUE_ID+1))
		done <<< "$FOUND_ID_FULL_STR"

		# Before: viewBox
		FIND="viewBox"
		REPLACE="id=\"$SVG_ANCHOR\" class=\"icon\" $FIND"
		# After: id="achor" class="icon" viewBox
		# First occurrence only
		echo "${ICON/$FIND/$REPLACE}" >> "$CURRENT_THEME.svg"

		((THEME_INDEX=THEME_INDEX+1))
		continue
	fi

	# End of icon
	if [[ $line = *'}'* ]]; then
		if [[ -z "${CHECK_THEMES// }" ]]; then
			echo "</tr>" >> $HTML_PREVIEW
			continue
		else
			TRIM=$(echo $CHECK_THEMES | xargs)
			echo -e "\e[1;31mError: theme \e[0;41m$TRIM\e[0m\e[1;31m is missing from SVG icon: \e[0;41m$SVG_ANCHOR\e[0m"
			echo "Check file: ${EXT_PATH}$JSON"
			exit 1
		fi
		continue
	fi

done < $JSON

for i in "${!THEMES[@]}"
do
	# Close SVG file
	echo "</svg>" >> "${THEMES[$i]}.svg"
done
# Close preview
echo "</table></body></html>" >> $HTML_PREVIEW

exit 0
