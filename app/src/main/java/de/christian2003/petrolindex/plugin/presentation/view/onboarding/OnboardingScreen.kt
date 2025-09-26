package de.christian2003.petrolindex.plugin.presentation.view.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import de.christian2003.petrolindex.R
import kotlinx.coroutines.launch


@Composable
fun OnboardingScreen(
    onNavigateUp: () -> Unit
) {
    val pagerState: PagerState = rememberPagerState(pageCount = { 3 })
    val scope: CoroutineScope = rememberCoroutineScope()

    Scaffold { innerPadding ->
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> OnboardingPage(
                        painter = painterResource(R.drawable.onboarding_traffic),
                        title = stringResource(R.string.onboarding_page1_title),
                        text = stringResource(R.string.onboarding_page1_text),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        foregroundColor = MaterialTheme.colorScheme.onPrimary
                    )
                    1 -> OnboardingPage(
                        painter = painterResource(R.drawable.onboarding_receipt),
                        title = stringResource(R.string.onboarding_page2_title),
                        text = stringResource(R.string.onboarding_page2_text),
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        foregroundColor = MaterialTheme.colorScheme.onSecondary
                    )
                    2 -> OnboardingPage(
                        painter = painterResource(R.drawable.onboarding_analysis),
                        title = stringResource(R.string.onboarding_page3_title),
                        text = stringResource(R.string.onboarding_page3_text),
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                        foregroundColor = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
            BottomRow(
                page = pagerState.currentPage,
                pageCount = pagerState.pageCount,
                onNextClick = {
                    if (pagerState.currentPage == pagerState.pageCount - 1) {
                        onNavigateUp()
                    }
                    else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                onPreviousClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                nextButtonVisible = true
            )
        }
    }
}


/**
 * Displays the row at the bottom through which the user can navigate through the onboarding pages.
 *
 * @param page              Index of the onboarding page currently displayed.
 * @param pageCount         Number of pages.
 * @param onNextClick       Callback invoked to navigate to the next page.
 * @param onPreviousClick   Callback invoked to navigate to the previous page.
 * @param modifier          Modifier.
 * @param nextButtonVisible Indicates whether the "next" button is visible.
 */
@Composable
private fun BottomRow(
    page: Int,
    pageCount: Int,
    onNextClick: () -> Unit,
    onPreviousClick: ()  -> Unit,
    modifier: Modifier = Modifier,
    nextButtonVisible: Boolean = true
) {
    val color: Color = when (page) {
        0 -> MaterialTheme.colorScheme.onPrimary
        1 -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onTertiary
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalDivider(color = color)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            if (page != 0) {
                TextButton(
                    onClick = onPreviousClick,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = color
                    ),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(stringResource(R.string.button_previous))
                }
            }
            Row(
                modifier = Modifier.align(Alignment.Center)
            ) {
                repeat(pageCount) { i ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (i == page) { color } else { color.copy(0.6f) })
                            .size(8.dp)
                    )
                }
            }
            if (nextButtonVisible) {
                TextButton(
                    onClick = onNextClick,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = color
                    ),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(if (page != pageCount - 1) { stringResource(R.string.button_next) } else { stringResource(R.string.button_finish) })
                }
            }
        }
    }
}


/**
 * Displays a static onboarding page consisting of an image, title and text.
 *
 * @param painter           Painter for the page image.
 * @param title             Title for the page.
 * @param text              Text for the page.
 * @param backgroundColor   Background color.
 * @param foregroundColor   Foreground color.
 * @param modifier          Modifier.
 */
@Composable
private fun OnboardingPage(
    painter: Painter,
    title: String,
    text: String,
    backgroundColor: Color,
    foregroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_onboarding))
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        )
        Column(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
        ) {
            Text(
                text = title,
                color = foregroundColor,
                style = MaterialTheme.typography.headlineLargeEmphasized,
                modifier = Modifier.padding(
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
            )
            Text(
                text = text,
                color = foregroundColor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
            )
        }
    }
}
