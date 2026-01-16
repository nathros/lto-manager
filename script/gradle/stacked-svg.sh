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
if [ $1 = "icon" ]; then
	THEMES=(tabler remix)
	THEMES_DIR=(tabler-icons RemixIcon)
	THEMES_VIEWBOX=('viewBox="0 0 24 24"' 'viewBox="0 0 24 24"')
	JSON=icons.json
elif [ $1 = "file" ]; then
	echo -e "\e[1;31mError: file not ready icon only\e[0m"
	exit 1
	#THEMES=(tabler remix)
	#THEMES_DIR=(tabler-icons RemixIcon)
	#THEMES_VIEWBOX=('viewBox="0 0 24 24"' 'viewBox="0 0 24 32"')
	#JSON=icons.json
else
	echo -e "\e[1;31mError: \e[0;41mMissing operation\e[0m\e[1;31m can accept \e[0;41micon\e[0m\e[1;31m or \e[0;41mfile\e[0m"
	exit 1
fi
# Limitation: the viewbox for all icons in a theme must be the same

cd "$(dirname "$0")"
SVG_ANCHOR=""
CHECK_THEMES=""
ROOT_DIR="../../"
EXT_PATH=external/

function print_rem() {
	echo --
	for i in "${CHECK_THEMES[@]}"
	do
	   echo "$i"
	done
	echo --
}

echo Using $1 themes:
printf "%-7s | %-22s | %-45s | %s" "Theme" "Path" "Repo" "ViewBox"
echo
for i in "${!THEMES[@]}"
do
	GIT_REPO=$(cat ../../.git/config | grep ${THEMES_DIR[$i]})
	GIT_REPO=${GIT_REPO#*=}
	TMP=`echo "${THEMES_VIEWBOX[$i]}" | cut -d'"' -f 2`
	printf "%-7s | %-22s | %-45s | %s\n" ${THEMES[$i]} ${EXT_PATH}${THEMES_DIR[$i]} $GIT_REPO "$TMP"

	# Create stacked SVG file
	echo "<svg xmlns=\"http://www.w3.org/2000/svg\" ${THEMES_VIEWBOX[$i]}>" > "${THEMES[$i]}.svg"
	echo "<defs><style>svg .icon { display: none } svg .icon:target { display: inline }</style></defs>" >> "${THEMES[$i]}.svg"
done
echo

echo "Fetching submodules at: ${ROOT_DIR}${EXT_PATH}"
echo "First run may take a few minutes"
git submodule update --init --recursive
echo

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
		echo Processing icon: $SVG_ANCHOR
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
		FOUND=false
		for i in "${!THEMES[@]}"
		do
			if [[ ${THEMES[$i]} == $CURRENT_THEME ]]; then
				# echo "$CURRENT_THEME==${THEMES_VIEWBOX[$i]}"
				ICON=$(cat "${ROOT_DIR}${CURRENT_PATH}")
				FIND=${THEMES_VIEWBOX[$i]}
				if [[ $ICON != *"$FIND"* ]]; then
					echo -e "\e[1;31mError: \e[0;41m$CURRENT_PATH\e[0m\e[1;31m file does not have expected \e[0;41m$FIND\e[0m"
					exit 1
				fi
				REPLACE="id=\"$SVG_ANCHOR\" class=\"icon\" $FIND"
				# echo "find $FIND"
				# echo "REPLACE $REPLACE"
				echo "${ICON/$FIND/$REPLACE}" >> "${THEMES[$i]}.svg"
				FOUND=true
				break
			fi
		done

		if [ FOUND = false ]; then
			echo -e "\e[1;31mError: viewBox THEMES_VIEWBOX size mismatch\e[0m"
			exit 1
		fi
	fi
	
	# End of icon
	if [[ $line = *'}'* ]]; then
		if [[ -z "${CHECK_THEMES// }" ]]; then
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

exit 0
